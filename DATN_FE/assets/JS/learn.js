const learnController = function ($scope, $http, $location, $rootScope, $window) {
  const token = $window.sessionStorage.getItem("jwt_token");
  const host = "http://localhost:8080/api";
  const progressUrl = `${host}/video_progress/save`; // Địa chỉ API lưu tiến độ video
  let id = $location.search().id;
  let registeredCourseId = $location.search().registeredCourseId; // Lấy registeredCourseId
  console.log("token :" + token);
  console.log("Course ID:", id);
  console.log("Registered Course ID:", registeredCourseId); // Lấy ID khóa học từ query string

  $scope.loading = 0;
  $scope.course = {};
  $scope.listComment = [];
  $scope.hashtags = [];
  $scope.showHashtags = false;
  $scope.showRelatedCourses = false;
  $scope.relatedCourses = [];
  $scope.user = {};
  $scope.totalLession = 0; // Tổng số bài học
  $scope.totalLessionComplete = 0; // Số bài học đã hoàn thành
  $scope.progressPercentage = 0; // Tổng tiến độ khóa học (phần trăm)
  $scope.totalTestComplete = 0; //tổng quiz đã hoàn thành

  $scope.saveVideoProgress = function (lessonId, videoPath, videoProgress) {
    if (!token) {
      console.log("Không có token, yêu cầu người dùng đăng nhập lại");
      return;
    }
    // Gọi API để lấy tiến độ video từ CSDL
    $http
      .get(`http://localhost:8080/api/video_progress/${lessonId}`, {
        headers: {
          Authorization: `Bearer ${token}`, // Đảm bảo token được gửi đúng cách
        },
      })
      .then((response) => {
        const videoProgressInDB = response.data;

        // Nếu đã đạt 100%, không cập nhật nữa
        if (videoProgressInDB && videoProgressInDB.videoProgress >= 100) {
          console.log("Video đã đạt 100%, không cần cập nhật tiến độ.");
          return;
        }

        // Tăng số bài học hoàn thành nếu đạt 100%
        if (videoProgress >= 100) {
          $scope.totalLessionComplete += 1;
        }

        // Cập nhật tiến độ khóa học
        $scope.updateCourseProgress();

        // Chuẩn bị dữ liệu để lưu tiến độ
        const progressData = {
          registeredCourseId: registeredCourseId,
          lessonId: lessonId,
          pathVideo: videoPath,
          videoProgress: videoProgress,
          update_at: new Date(),
        };

        // Gọi API POST để lưu tiến độ video
        return $http.post(progressUrl, progressData, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
      })
      .then((response) => {
        if (response) {
          console.log("Lưu tiến độ video thành công", response.data);
        }
      })
      .catch((error) => {
        // Nếu không có dữ liệu (404), tạo mới tiến độ video
        if (error.status === 404) {
          console.log("Không có tiến độ video, tạo mới...");

          const progressData = {
            registeredCourseId: registeredCourseId,
            lessonId: lessonId,
            pathVideo: videoPath,
            videoProgress: videoProgress,
            update_at: new Date(),
          };

          $http
            .post(progressUrl, progressData, {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            })
            .then((response) => {
              console.log("Tạo tiến độ video mới thành công", response.data);
            })
            .catch((error) => {
              console.log("Lỗi khi tạo tiến độ video mới", error);
            });
        } else {
          console.log("Lỗi khi lưu tiến độ video", error);
        }
      });
  };

  $scope.gotoCourseDetail = function (courseId) {
    $location
      .path("/assets/views/user/chi_tiet_khoa_hoc")
      .search({ id: courseId });
    window.scrollTo(0, 0);
  };

  $scope.gotoTest = function (testId) {
    $location.path("/assets/views/user/take_the_test").search({ id: testId });
    window.scrollTo(0, 0);
  };
  $scope.lessonsProgress = []; // Mảng lưu tiến độ của các bài học trong khóa học

  $scope.updateCourseProgress = function () {
    if ($scope.totalLession > 0) {
      const newProgress =
        ($scope.totalLessionComplete + $scope.totalTestComplete) /
        ($scope.totalLession + $scope.totalQuiz) * 100;

      if (isNaN(newProgress) || newProgress < 0 || newProgress > 100) {
        console.error("Tiến độ không hợp lệ:", newProgress);
        return;
      }

      if (newProgress > $scope.progressPercentage) {
        $scope.progressPercentage = Math.round(newProgress); // Làm tròn tiến độ khóa học về số nguyên

        const progressBar = document.getElementById("progressBarCourse");
        const progress = ($scope.totalLessionComplete + $scope.totalTestComplete) /
          ($scope.totalLession + $scope.totalQuiz) * 100;
        progressBar.style.setProperty("--progress", progress.toFixed(0));

        if (progress >= 99) {
          progressBar.style.backgroundColor = "MediumAquamarine";
        } else {
          progressBar.style.backgroundColor = "IndianRed";
        }

        document.querySelector("span#completionTick").style.display = newProgress >= 100 ? 'inline' : 'none'; // Hiển thị dấu tick khi hoàn thành

        console.log("Tiến độ khóa học cập nhật:", $scope.progressPercentage);

        const courseId = parseInt(id, 10);
        if (isNaN(courseId)) {
          console.error("ID khóa học không hợp lệ:", id);
          return;
        }

        const courseProgressData = {
          currentLessionId: $scope.selectedLesson?.lessonId || null,
          totalLession: $scope.totalLession,
          totalQuiz: $scope.totalQuiz,
          totalLessionComplete: $scope.totalLessionComplete,
          totalTestComplete: $scope.totalTestComplete,
          progressPercentage: $scope.progressPercentage, // Sử dụng số nguyên
          progressStatus: 0,
        };

        console.log("Dữ liệu gửi lên backend:", courseProgressData);

        // Gửi yêu cầu POST tới API cập nhật tiến độ khóa học
        $http
          .post(
            `http://localhost:8080/api/course_progress/update-progress/${courseId}`,
            courseProgressData,
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
          )
          .then((response) => {
            console.log("Cập nhật tiến độ khóa học thành công:", response.data);
          })
          .catch((error) => {
            console.error("Lỗi khi cập nhật tiến độ khóa học:", error);
          });
      }
    }
  };

  $scope.trackVideoProgress = function (lessonId) {
    if (!lessonId) {
      console.error("Không có lessonId hợp lệ.");
      return;
    }

    const videoElement = document.getElementById("myVideo");
    const progressBar = document.getElementById("progressBar");

    videoElement.addEventListener("timeupdate", () => {
      const progress = (videoElement.currentTime / videoElement.duration) * 100;
      progressBar.style.setProperty("--progress", progress.toFixed(0));

      if (progress >= 99) {
        progressBar.style.backgroundColor = "MediumAquamarine";
      } else {
        progressBar.style.backgroundColor = "IndianRed";
      }
    });

    videoElement.addEventListener("pause", function () {
      const progress = (videoElement.currentTime / videoElement.duration) * 100;
      const videoProgress = videoElement.currentTime;
      $scope.saveVideoProgress(
        lessonId,
        videoElement.src,
        progress.toFixed(0)
      );
      // }
    });
  };

  window.onload = function () {
    // Giả sử bạn có thể lấy lessonId từ các đối tượng hoặc trạng thái của video hiện tại
    const lessonId = $scope.selectedLesson
      ? $scope.selectedLesson.lessonId
      : null;

    if (!lessonId) {
      console.error("Không có lessonId hợp lệ.");
      return;
    }

    // Lấy tiến độ video từ API
    $http({
      method: "GET",
      url: `http://localhost:8080/api/video_progress/${lessonId}`,
      headers: {
        Authorization: `Bearer ${token}`, // Đảm bảo token được truyền đúng
      },
    })
      .then((response) => {
        const videoProgress = response.data;

        if (videoProgress) {
          const videoElement = document.getElementById("myVideo");
          const progressBar = document.getElementById("progressBar");

          // Cập nhật thanh tiến độ
          const progress =
            (videoProgress.videoProgress / videoElement.duration) * 100;
          progressBar.style.setProperty("--progress", progress.toFixed(0));

          // Cập nhật thời gian video
          videoElement.currentTime = videoProgress.videoProgress;
        }
      })
      .catch((error) => {
        console.log("Lỗi khi lấy tiến độ video:", error);
      });
  };

  // Khi video được chọn
  $scope.showLessonDetail = function (lesson) {
    console.log("Lesson được chọn:", lesson);
    $scope.selectedLesson = lesson;

    //selectedLesson.pathVideo
    //Hiển thị video không có link clouudinary
    console.log($scope.selectedLesson.pathVideo);    
    $scope.loadBlobVideo($scope.selectedLesson.pathVideo);

    // Gọi API để lấy tiến độ video từ CSDL
    if (lesson && lesson.lessonId) {
      $http({
        method: "GET",
        url: `http://localhost:8080/api/video_progress/${lesson.lessonId}`,
        headers: {
          Authorization: `Bearer ${token}`, // Đảm bảo token được truyền đúng
        },
      })
        .then((response) => {
          const videoProgress = response.data;

          if (videoProgress) {
            // Tính toán tiến độ video và cập nhật thanh tiến độ
            const progress = (videoProgress.videoProgress / lesson.duration) * 100; // Giả sử bạn có thuộc tính `duration` cho video
            const progressBar = document.getElementById("progressBar");

            // Cập nhật giá trị thanh tiến độ
            progressBar.style.setProperty("--progress", progress.toFixed(0));

            // Thay đổi màu sắc của thanh tiến độ
            if (progress >= 100) {
              progressBar.style.backgroundColor = "MediumAquamarine"; // Màu xanh khi tiến độ đạt 100%
            } else {
              progressBar.style.backgroundColor = "IndianRed"; // Màu đỏ khi tiến độ chưa đạt 100%
            }

            // Cập nhật thời gian video
            const videoElement = document.getElementById("myVideo");
            videoElement.currentTime = videoProgress.videoProgress;

            // Cập nhật tiến độ khóa học
            $scope.updateCourseProgress(); // Cập nhật thanh tiến độ khóa học
          }

        })
        .catch((error) => {
          console.log("Lỗi khi lấy tiến độ video:", error);
        });

      $scope.trackVideoProgress(lesson.lessonId); // Theo dõi tiến độ video cho bài học
    } else {
      console.log("Lỗi: Không có ID bài học hợp lệ.");
    }
  };

  //Hiển thị video ẩn link
  //Chỉ truyền link video qua rồi trả vè file blob thôi
  //Nếu muốn đúng thì phải trả courseId qua kia mới đúng
  $scope.blobVideo = "";
  $scope.loadBlobVideo = function (path) {
    console.log("Vào phương thức load video");
    $scope.loading = 1;
    // $scope.path = "http://res.cloudinary.com/dxj6jmdm8/video/upload/v1735464665/Sun%20Dec%2029%2016:30:52%20ICT%202024.mp4";
    console.log($scope.path);    
    $http
      .get(`http://localhost:8080/api/v1/upload-file/download-video`, {
        params: {
          link: path,
          courseId: 1,
          tokenString: token,
        },
        responseType: "arraybuffer",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((response) => {
        $scope.loading = 0;
        let blob = new Blob([response.data], { type: "video/mp4" });
        console.log("Blob size:", blob.size); // Kiểm tra kích thước blob
        if (blob.size > 0) {
          $scope.blobVideo = URL.createObjectURL(blob);
        } else {
          console.error("Video blob is empty");
        }
      })
      .catch((err) => {
        $scope.loading = 0;
        console.error("Error", err);
      });
  };

  // Cập nhật hàm load_course
  $scope.load_course = function () {
    let urlKhoaHoc = `${host}/course/course-detail/${id}`;
    $http
      .get(urlKhoaHoc, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((resp) => {
        $scope.course = resp.data;

        // Tính tổng số bài học
        $scope.totalLession = 0;
        $scope.totalQuiz = 0; // Thêm biến đếm quiz

        if ($scope.course.listSectionCourseDetailDTO) {
          $scope.course.listSectionCourseDetailDTO.forEach((section) => {
            // Đếm số bài học
            if (section.listLesson) {
              $scope.totalLession += section.listLesson.length;
            }

            // Đếm số quiz
            if (section.listTest) {
              $scope.totalQuiz += section.listTest.length;
            }
          });
        }

        console.log("Tổng số bài học:", $scope.totalLession);
        console.log("Tổng số quiz:", $scope.totalQuiz);

        // Gán cứng giá trị cho totalQuizComplete (bạn có thể thay đổi sau nếu cần logic tính toán)
        $scope.totalQuizComplete = $scope.totalQuiz; // Gán cứng tổng số quiz đã hoàn thành

        // Tính lại tổng tiến độ nếu đã có số bài học hoàn thành
        $scope.updateCourseProgress();
      })
      .catch((err) => {
        console.log("Error", err);
      });
  };

  // Khởi tạo tải khóa học và mục lục
  $scope.load_course();

  // Các hàm khác như load comment, hashtags, ...
  $scope.loadListComment = function () {
    $http
      .get(`http://localhost:8080/api/reply/courseId/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((response) => {
        $scope.listComment = response.data;
      })
      .catch((error) => {
        console.error("Lỗi khi gọi API:", error);
        alert("Không thể lấy dữ liệu khóa học. Vui lòng thử lại!");
      });
  };
  $scope.loadListComment();

  // Hàm để tải hashtags theo courseId
  $scope.loadHashtag = function () {
    console.log("courseId:", id); // Kiểm tra courseId
    $http
      .get(`http://localhost:8080/api/hashtag/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(function (resp) {
        console.log("Dữ liệu từ API:", resp.data); // Kiểm tra dữ liệu API trả về
        if (Array.isArray(resp.data)) {
          // Kiểm tra dữ liệu trả về là một mảng
          $scope.hashtags = resp.data; // Thiết lập dữ liệu phản hồi vào mảng hashtags
          $scope.showHashtags = true; // Đảm bảo rằng hashtags được hiển thị
        } else {
          console.log("Dữ liệu trả về không phải là mảng", resp.data);
        }
      })
      .catch(function (err) {
        console.log("Lỗi không thể tải dữ liệu", err); // Ghi log bất kỳ lỗi nào
      });
  };
  //
  $scope.loadRelatedCoursesKhoa = function () {
    $http
      .get(`http://localhost:8080/api/hashtag/hashtagKhoa/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(function (resp) {
        // Giả sử bạn muốn lọc lại các khóa học chỉ có hashtagId nhất định
        $scope.listCourseOfHashtag = resp.data;
        console.log("Hashtag:", $scope.listCourseOfHashtag);
      })
      .catch(function (err) {
        console.log("Lỗi không thể tải khóa học liên quan", err);
      });
  };

  $scope.load_course_hashtag = function (hashTagId) {
    console.log(hashTagId);
    let urlKhoaHoc = `http://localhost:8080/api/course/findCourseHashTag/${hashTagId}`;
    $http
      .get(urlKhoaHoc, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((resp) => {
        $rootScope.listKhoaHoc = resp.data; // Lưu khóa học vào $rootScope
        $location
          .path("/assets/views/user/danh_muc_khoa_hoc")
          .search({ hashTagId: hashTagId });
        window.scrollTo(0, 0);
      })
      .catch((err) => {
        console.log("Error", err);
      });
  };

  $scope.setRating = function (rating) {
    $scope.comment = {
      statRating: 0,
    };
    $scope.comment.statRating = rating; // Gán giá trị sao được chọn
  };

  $scope.addCMT = function () {
    if (!$scope.comment.statRating || $scope.comment.statRating === 0) {
      // Hiển thị thông báo lỗi bằng SweetAlert2
      Swal.fire({
        icon: "warning", // Biểu tượng cảnh báo
        title: "Thiếu số sao",
        text: "Vui lòng chọn số sao trước khi gửi đánh giá.",
        confirmButtonText: "OK",
      });
      return; // Dừng hàm nếu không hợp lệ
    }

    const comment = {
      courseId: id,
      content: $scope.comment.content,
      starRating: $scope.comment.statRating,
    };

    $http({
      method: "POST",
      url: `http://localhost:8080/api/v1/comment/${token}`,
      data: comment,
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then(function (resp) {
        Swal.fire({
          icon: "success", // Biểu tượng thành công
          title: "Thành công",
          text: "Đánh giá của bạn đã được gửi.",
          confirmButtonText: "OK",
        });
        $scope.listComment.push(resp.data);
        $scope.comment.content = "";
        $scope.comment.statRating = 0;
        $scope.loadListComment();
      })
      .catch(function (err) {
        Swal.fire({
          icon: "error", // Biểu tượng lỗi
          title: "Lỗi",
          text: "Không thể thêm đánh giá mới. Vui lòng thử lại.",
          confirmButtonText: "OK",
        });
        console.error("Lỗi không thể thêm comment mới", err);
      });
  };

  // PHẦN NHẬN CHỨNG CHỈ
  // - xử lý nút chứng chỉ
  $scope.listCourseProgress = {}; // Biến lưu tiến trình khóa học
  $scope.getCourseProgress = function () {
    if (!id || !token) {
      console.error(
        "ID hoặc token không hợp lệ. Vui lòng kiểm tra và thử lại."
      );
      alert("Thông tin không hợp lệ. Không thể lấy tiến trình khóa học.");
      return;
    }

    const urlCourseProgress = `http://localhost:8080/api/v1/user/GetCourseProgress/${id}`;

    $http
      .get(urlCourseProgress, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((response) => {
        $scope.listCourseProgress = response.data;
        console.log(
          "Tiến trình khóa học đã được tải:",
          $scope.listCourseProgress
        );

        // Lấy trực tiếp progressPercentage từ dữ liệu API trả về
        $scope.progressPercentage =
          $scope.listCourseProgress.progressPercentage;
          const progressBar = document.getElementById("progressBarCourse");
          const progress =  $scope.progressPercentage;
          progressBar.style.setProperty("--progress", progress.toFixed(0));
          if (progress >= 99) {
            progressBar.style.backgroundColor = "MediumAquamarine";
            
          } else {
            progressBar.style.backgroundColor = "IndianRed";
          }

        // Cập nhật thanh tiến độ
        // document
        //   .getElementById("progressBarCourse")
        //   .style.setProperty("--progress", $scope.progressPercentage + "%");
      })
      .catch((error) => {
        console.error("Lỗi không thể tải dữ liệu:", error);
        alert("Không thể tải tiến trình khóa học. Vui lòng thử lại sau.");
      });

  };

  // Gọi hàm lấy tiến trình khóa học
  $scope.getCourseProgress();

  $scope.receiveCertificate = function () {
    if (!token || !id) {
      Swal.fire({
        title: "Token hoặc ID khóa học không hợp lệ!",
        icon: "warning",
      });
      return;
    }
  
    Swal.fire({
      title: "Bạn có muốn nhận chứng chỉ không?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Có",
      cancelButtonText: "Hủy",
    }).then((result) => {
      if (result.isConfirmed) {
        Swal.fire({
          title: "Đang xử lý...",
          allowOutsideClick: false,
          didOpen: () => {
            Swal.showLoading();
          },
        });
  
        $http
          .post(
            `http://localhost:8080/api/v1/user/sendCertificate/${id}`,
            null,
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
          )
          .then((resp) => {
            Swal.fire({
              title: resp.data.message,
              icon: "success",
            });
            $window.location.reload();
          })
          .catch((err) => {
            const message =
              err.data?.message ||
              (err.status === -1
                ? "Không thể kết nối đến server."
                : "Có lỗi xảy ra. Vui lòng thử lại sau.");
            Swal.fire({
              title: message,
              icon: "error",
            });
          });
      }
    });
  };

  // - Tác giả
  $scope.tacGiaKhoaHoc = function () {
    const urlTacGia = `http://localhost:8080/api/v1/user/email/lehoanghuycoder@gmail.com`;
    $http.get(urlTacGia, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(function (response) {
        if (response.data) {
          $scope.user = response.data; // Cập nhật dữ liệu form với phản hồi từ server
          console.log("Thông tin người dùng:", response.data);
        } else {
          alert("Không tìm thấy dữ liệu người dùng.");
        }
      })
      .catch(function (error) {
        console.error("Lỗi khi lấy dữ liệu người dùng:", error);
        alert("Đã xảy ra lỗi khi lấy thông tin người dùng. Vui lòng thử lại sau.");
      });
  }
  $scope.tacGiaKhoaHoc();
  //----
  $scope.loadHashtag();
  $scope.loadRelatedCoursesKhoa();
  // Các hàm khác của bạn...
};
app.controller("learnController", learnController);
