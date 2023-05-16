// main.js
var express = require("express");
var bodyParser = require("body-parser");
var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

//db 연결

const XLSX = require("xlsx");

// XLSX 파일 경로
const filePath = "cafedata.xlsx";

// XLSX 파일 로드
const workbook = XLSX.readFile(filePath);

// 첫 번째 시트 선택
const sheetName = workbook.SheetNames[0];
const sheet = workbook.Sheets[sheetName];

// 데이터 정리
const data = XLSX.utils.sheet_to_json(sheet, { header: 1 });

// 필요한 데이터 선택
const headers = data[0];
const cleanedData = data.slice(1).map((row) => {
  const rowData = {};
  headers.forEach((header, index) => {
    rowData[header] = row[index];
  });
  return rowData;
});

// 정리된 데이터 확인

function sortByEmotion(emotion) {
  return function (a, b) {
    return b[emotion] - a[emotion];
  };
}
const emotionOrder = ["분노", "싫음", "두려움", "행복", "슬픔", "놀람", "중립"];

cafesortedlist = new Array(6).fill(null);
for (let i = 0; i < 6; i++) {
  cafesortedlist[i] = [...cleanedData].sort(
    (a, b) => b[emotionOrder[i]] - a[emotionOrder[i]]
  );
}

try {
  app.listen(3005, "0.0.0.0", function () {
    console.log(`${new Date().toLocaleString("ko-kr")} : 서버 실행 중...`);
  });
} catch (error) {
  exit(-1);
}

app.post("/getCafe", async function (req, res) {
  console.log(`${new Date().toLocaleString("ko-kr")} [건국카베기생]`);
  console.log(req.body);
  var emotion = req.body.emotion;
  var message = "정상작동";
  var resultCode = 404;
  try {
    if (typeof emotion == "number") {
      message;
      resultCode = 200;
    } else {
      emotion = emotionOrder.indexOf(emotion);
      resultCode = 200;

      if (emotion == -1) {
        emotion = 0;
        resultCode = 500;
        res.json({
          code: resultCode,
          message: "감정을 확인해주세요",
          cafelist: cafesortedlist[emotion].slice(0, 10),
        });
        return;
      }
    }
    res.json({
      code: resultCode,
      message: "정상작동",
      cafelist: cafesortedlist[emotion].slice(0, 10),
    });
  } catch (error) {
    console.log(error);
    res.json({
      code: 500,
      message: "정상작동",
      cafelist: cafesortedlist[emotion].slice(0, 10),
    });
  }

  //
});
