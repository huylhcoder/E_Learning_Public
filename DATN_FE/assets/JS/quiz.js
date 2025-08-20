app.controller("quizController", function ($scope, $http, $window, $location) {
  const token = $window.sessionStorage.getItem("jwt_token");
  console.log(token);
  if (token == null) {
    $window.location.href =
      "http://127.0.0.1:5500/index.html#!/assets/views/dang_nhap";
  }

  // Lấy giá trị của id từ search parameters trong URL
  $scope.id = $location.search().id;
  $scope.quizs = {};
  $scope.selectedAnswers = []; //Một list chứa đáp án đã chọn
  $scope.pageSize = 1;
  $scope.start = 0;
  $scope.submissionTime = null;
  $scope.isSubmitted = false;
  const userId = 2;


  // Khởi tạo khi load trang
  $scope.init = function () {
    //const isSubmitted = sessionStorage.getItem("isSubmitted");

    if ($scope.isSubmitted === "true") {
      $scope.isSubmitted = true;
      $scope.thangDiemMuoi = sessionStorage.getItem("thangDiemMuoi");
      $scope.soCauDung = sessionStorage.getItem("soCauDung");
      $scope.phanTramDatDuoc = sessionStorage.getItem("phanTramDatDuoc");
      $scope.dat = sessionStorage.getItem("dat");
      $scope.submissionTime = sessionStorage.getItem("submissionTime");

      // Tải lại danh sách câu hỏi từ sessionStorage
      const savedQuestions = sessionStorage.getItem("quizQuestions");
      if (savedQuestions) {
        $scope.quizs.listQuestion = JSON.parse(savedQuestions);
      } else {
        $scope.quizs.listQuestion = []; // Nếu không có câu hỏi, gán mảng rỗng
      }
      $scope.showTest();
    } else {
      $scope.isSubmitted = false;

      // Lấy danh sách câu hỏi từ API
      $http
        .get("http://localhost:8080/api/v1/test/take-the-test/" + $scope.id, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then(
          function (res) {
            if (
              res.data &&
              res.data.listQuestion &&
              res.data.listQuestion.length > 0
            ) {
              $scope.quizs = res.data;
              // $scope.selectedAnswers = new Array(res.data.listQuestion.length).fill(null).map(() => ({}));
              // sessionStorage.setItem("quizQuestions", JSON.stringify(res.data.listQuestion));
              // Gọi hàm để tải danh sách đáp án đã chọn
              $scope.loadSelectedAnswers();
            } else {
              $scope.quizs = { listQuestion: [] };
            }
          },
          function (err) {
            console.error("Lỗi khi request môn học:", err);
            alert(
              "Lỗi khi request môn học: " + err.status + " - " + err.statusText
            );
            $scope.quizs = { listQuestion: [] };
          }
        );
    }
  };

  $scope.showTest = function () {
    $http
      .get("http://localhost:8080/api/v1/test/take-the-test/" + $scope.id, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(
        function (res) {
          if (
            res.data &&
            res.data.listQuestion &&
            res.data.listQuestion.length > 0
          ) {
            $scope.quizs = res.data;
            console.log($scope.quizs);
            // $scope.selectedAnswers = new Array(res.data.listQuestion.length).fill(null).map(() => ({}));
            // sessionStorage.setItem("quizQuestions", JSON.stringify(res.data.listQuestion));
            // Gọi hàm để tải danh sách đáp án đã chọn
            $scope.loadSelectedAnswers();
          } else {
            $scope.quizs = { listQuestion: [] };
          }
        },
        function (err) {
          console.error("Lỗi khi request môn học:", err);
          alert(
            "Lỗi khi request môn học: " + err.status + " - " + err.statusText
          );
          $scope.quizs = { listQuestion: [] };
        }
      );
  };

  // Hàm để tải danh sách đáp án đã chọn
  $scope.loadSelectedAnswers = function () {
    $http
      .get(
        `http://localhost:8080/api/v1/user_answer_history/get-answers/${$scope.id}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      )
      .then(
        function (answerRes) {
          if (answerRes.data) {
            console.log(answerRes.data);
            answerRes.data.forEach((answer) => {
              const questionId = answer.question.questionId; // Lấy questionId
              const answerId = answer.answer.answerId; // Lấy answerId

              // Đảm bảo selectedAnswers đã được khởi tạo
              if (!$scope.selectedAnswers[questionId - 1]) {
                $scope.selectedAnswers[questionId - 1] = {};
              }
              // Gán answerId vào selectedAnswers
              $scope.selectedAnswers[questionId - 1].answerId = answerId;
            });
            // Cập nhật sessionStorage
            sessionStorage.setItem(
              "selectedAnswers",
              JSON.stringify($scope.selectedAnswers)
            );
          }
        },
        function (error) {
          console.error("Lỗi khi lấy đáp án đã làm:", error);
        }
      );
  };

  // Gọi hàm loadSelectedAnswers khi controller được khởi tạo
  $scope.loadSelectedAnswers();

  // Gọi init khi controller được load
  $scope.init();

  $scope.selectAnswer = function (questionId, answerId) {
    questionId = Number(questionId);
    answerId = Number(answerId);
    if (!isNaN(questionId) && !isNaN(answerId)) {
      // $scope.selectedAnswers[questionId - 1] = { questionId: questionId, answerId: answerId };
      // sessionStorage.setItem("selectedAnswers", JSON.stringify($scope.selectedAnswers));
      // Gửi yêu cầu POST với FormData để cập nhật đáp án
      var formData = new FormData();
      formData.append("maBaiKiemTra", $scope.id); // mã bài kiểm tra
      formData.append("maCauHoi", questionId);
      formData.append("maDapAn", answerId);

      $http
        .post(
          "http://localhost:8080/api/v1/user_answer_history/save-answer",
          formData,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": undefined,
            },
          }
        )
        .then(
          function (res) {
            console.log("Cập nhật đáp án thành công:", res.data);
            // Gọi lại hàm để tải danh sách đáp án đã chọn
            $scope.loadSelectedAnswers();
          },
          function (error) {
            console.error("Lỗi khi lưu đáp án:", error);
          }
        );
    }
  };
  $scope.isUserAnswer = function (questionId, answerId) {
    const userAnswer = $scope.selectedAnswers[questionId - 1];
    return userAnswer && userAnswer.answerId === answerId;
  };

  $scope.getSelectedAnswerText = function (questionId) {
    const userAnswer = $scope.selectedAnswers[questionId - 1];
    if (userAnswer) {
      const selectedAnswer = quizs.listQuestion[
        questionId - 1
      ].listAnswerDTO.find((dapAn) => dapAn.answerId === userAnswer.answerId);
      return selectedAnswer ? selectedAnswer.text : "Chưa chọn";
    }
    return "Chưa chọn";
  };

  //Lưu đáp án thành  công rồi
  //giả sử tắt ra vô lại làm sao để có hiển thị đáp án đã chọn của câu

  // Biến để theo dõi việc hiển thị chi tiết
  $scope.showingDetails = false;

  // Hàm để hiển thị chi tiết câu hỏi và đáp án
  $scope.showDetails = function () {
    //$scope.init();
    //$scope.showingDetails = !$scope.showingDetails; // Chuyển đổi trạng thái hiển thị
    console.log($scope.quizs);
    //console.log($scope.selectedAnswer);
  };

  // Kiểm tra nếu tất cả câu hỏi đã được trả lời
  $scope.validateAllAnswered = function () {
    return $scope.selectedAnswers.every((answer) => answer !== null);
  };

  // Xử lý nộp bài
  $scope.handleSubmit = function () {
    const totalQuestions = $scope.quizs.listQuestion.length;
    const answeredQuestions = $scope.selectedAnswers.filter(
      (answer) => answer !== null
    ).length;

    if (answeredQuestions < totalQuestions) {
      alert(
        `Bạn đã trả lời ${answeredQuestions}/${totalQuestions} câu hỏi. Vui lòng hoàn thành tất cả trước khi nộp bài.`
      );
      return;
    }

    if (!confirm("Bạn có chắc chắn muốn nộp bài?")) return;

    $http
      .post(
        `http://localhost:8080/api/v1/test/submit/${$scope.id}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      )
      .then(
        function (res) {
          alert("Nộp bài thành công!");
          const result = res.data;
          $scope.thangDiemMuoi = (result.score / result.totalQuestions) * 10;
          $scope.soCauDung = result.score;
          $scope.phanTramDatDuoc = result.percentage;
          $scope.dat =
            result.score >= result.totalQuestions / 2 ? "Đạt" : "Không đạt";
          $scope.submissionTime = new Date().toLocaleString();
          $scope.isSubmitted = true;

          // Lưu kết quả vào bảng user_test_result
          const resultPayload = {
            users_id: userId, // ID của người dùng
            test_id: $scope.quizs.testID, // ID của bài test
            score: $scope.thangDiemMuoi, // Điểm số
            completion_time: $scope.submissionTime, // Thời gian hoàn thành
            number_of_correct_answer: $scope.soCauDung, // Số câu trả lời đúng
            status: $scope.dat, // Trạng thái (Đạt/Không đạt)
          };

          // Gửi yêu cầu lưu kết quả vào bảng user_test_result
          $http
            .post(
              "http://localhost:8080/api/v1/user_test_result/save",
              resultPayload,
              {
                headers: {
                  Authorization: `Bearer ${token}`,
                },
              }
            )
            .then(
              function (saveRes) {
                console.log("Kết quả đã được lưu thành công:", saveRes.data);
              },
              function (error) {
                console.error("Lỗi khi lưu kết quả:", error);
              }
            );
          // Lưu kết quả vào sessionStorage
          sessionStorage.setItem("isSubmitted", "true");
          sessionStorage.setItem("thangDiemMuoi", $scope.thangDiemMuoi);
          sessionStorage.setItem("soCauDung", $scope.soCauDung);
          sessionStorage.setItem("phanTramDatDuoc", $scope.phanTramDatDuoc);
          sessionStorage.setItem("dat", $scope.dat);
          sessionStorage.setItem("submissionTime", $scope.submissionTime);
        },
        function (error) {
          alert("Lỗi khi nộp bài. Vui lòng thử lại!");
        }
      );
  };

  // Hàm xóa lịch sử đáp án
  $scope.clearAnswerHistory = function () {
    return $http
      .delete(`http://localhost:8080/api/v1/test/delete-answers/${$scope.id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(
        function (response) {
          console.log("Lịch sử đáp án đã được xóa thành công:", response.data);
        },
        function (error) {
          console.error("Lỗi khi xóa lịch sử đáp án:", error);
          alert(
            "Lỗi khi xóa lịch sử đáp án: " +
              error.status +
              " - " +
              error.statusText
          );
        }
      );
  };

  $scope.resetQuiz = function () {
    $scope.clearAnswerHistory().then(function () {
      $scope.selectedAnswers = "";
      sessionStorage.removeItem("selectedAnswers");
      // Kiểm tra xem quizs.listQuestion có hợp lệ không trước khi sử dụng length
      if ($scope.quizs && $scope.quizs.listQuestion) {
        $scope.selectedAnswers = new Array(
          $scope.quizs.listQuestion.length
        ).fill(null);
      } else {
        $scope.selectedAnswers = []; // Nếu không có listQuestion thì reset mảng câu trả lời
      }

      // Reset các thông tin liên quan đến kết quả bài thi
      $scope.isSubmitted = false;
      $scope.submissionTime = null;
      $scope.thangDiemMuoi = null;
      $scope.soCauDung = null;
      $scope.phanTramDatDuoc = null;
      $scope.dat = null;
      $scope.start = 0;

      // Xóa dữ liệu trong sessionStorage
      sessionStorage.removeItem("isSubmitted");
      sessionStorage.removeItem("thangDiemMuoi");
      sessionStorage.removeItem("soCauDung");
      sessionStorage.removeItem("phanTramDatDuoc");
      sessionStorage.removeItem("dat");
      sessionStorage.removeItem("submissionTime");
      sessionStorage.removeItem("quizQuestions"); // Xóa danh sách câu hỏi

      // Gọi lại hàm init để tải lại câu hỏi từ API
      $scope.init();
    });
  };

  $scope.goBack = function () {
    window.history.back();
  };

  $scope.isCorrectAnswer = function (question) {
    const answer = $scope.selectedAnswers[question.questionId - 1];
    return answer && answer.answerId === question.correctAnswerId; // Giả sử bạn có thuộc tính correctAnswerId trong câu hỏi
  };

  $scope.getCorrectAnswerText = function (question) {
    const correctAnswer = question.listAnswerDTO.find(
      (a) => a.answerId === question.correctAnswerId
    );
    return correctAnswer ? correctAnswer.text : "Không có đáp án đúng";
  };

  // Chức năng phân trang
  $scope.first = function () {
    $scope.start = 0;
  };
  $scope.prev = function () {
    if ($scope.start > 0) $scope.start -= $scope.pageSize;
  };
  $scope.next = function () {
    if ($scope.start < $scope.quizs.listQuestion.length - $scope.pageSize)
      $scope.start += $scope.pageSize;
  };
  $scope.last = function () {
    $scope.start = $scope.quizs.listQuestion.length - $scope.pageSize;
  };
});
