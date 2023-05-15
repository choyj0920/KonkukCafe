import threading
import time
import requests
import http.client
from codecs import encode
import json


# 첫 번째 API: 토큰 요청 API
token_url = 'https://oauth.api.lgthinqai.net:443/v1/cognito'  # 토큰 요청 API의 URL

# 토큰 요청 API에 필요한 파라미터들
token_data = {
    'Authorization': "Basic N2wyYjNmbG5zdnUwMWVsNGg3N3M2OTBqdTY6MWMxa3VvNWF1bDB0M2ZqcTZqdTZlc3A0ZGg5bjlsaGE5a3N0YzduNmwyY2VvYW1rZW12cA==",
    'Content-Type': "application/x-www-form-urlencoded"
}

# 30분마다 토큰을 갱신하는 스레드 함수
def update_token():
    global token # 전역변수 처리
    while True:
        # 토큰 갱신 코드
        time.sleep(1800)

        token = renew_token()

# 토큰 갱신 함수
def renew_token():
    
    # 토큰 요청 API 호출
    response = requests.post(token_url, headers=token_data)

    # 호출 결과에서 토큰 추출
    access_token = response.json()['access_token']
    # print("토큰 생성 :" +access_token)
    return access_token


# 감정 분석 API를 호출하는 함수
def call_api(inputText):
    # API 호출 코드
    
    conn = http.client.HTTPSConnection("korea.api.lgthinqai.net", 443)
    dataList = []
    boundary = 'wL36Yn8afVp8Ag7AmP8qZ0SA4n1v9T'
    dataList.append(encode('--' + boundary))
    dataList.append(encode('Content-Disposition: form-data; name=config;'))

    dataList.append(encode('Content-Type: {}'.format('text/plain')))
    dataList.append(encode(''))

    dataList.append(encode("{\"type\": \"EMOTION_RECOGNITION\",\"input\": {\"type\": \"TEXT\",\"text\": \""+inputText+"\"}}"))
    dataList.append(encode('--'+boundary+'--'))
    dataList.append(encode(''))
    body = b'\r\n'.join(dataList)
    payload = body
    headers = {
    'Authorization': token,
    'x-api-key': 'MTtlZjUyYzFmYTViMjc0NGNkYTg3NDE3NGYwOWU5NGQ0YTsxNjgxMDk2OTAzMzk1',
    'Cookie': 'AWSALB=V5f5E/YK2RYxefslFcBChE3tqNGX1oM/PR0HHGQfaxGin+YqmF5pGw9uYCIUD2PXQX7VHEj7JxkZyF5Jl3beDiEkD9STnNzWukJvfqz0y/NIYcrvKmhigVSxOe2m; AWSALBCORS=V5f5E/YK2RYxefslFcBChE3tqNGX1oM/PR0HHGQfaxGin+YqmF5pGw9uYCIUD2PXQX7VHEj7JxkZyF5Jl3beDiEkD9STnNzWukJvfqz0y/NIYcrvKmhigVSxOe2m',
    'Content-type': 'multipart/form-data; boundary={}'.format(boundary)
    }
    
    # 응답 처리 코드
    try:
        conn.request("POST", "/emotion/er/v1/recognition", payload, headers)
        res = conn.getresponse()
        jsondata = res.read().decode("utf-8")
        data= json.loads(jsondata)

        print(data["results"]["uni_modal"]['text'])
    except:
        print("error")


# 메인 함수
if __name__ == '__main__':
    # 스레드 생성 및 실행
    try:
        token = renew_token()
    except:
        exit(1)

    t = threading.Thread(target=update_token)
    t.start()

    # API 호출
    while True:
        # 현재 토큰 가져오기

        # API 호출
        try:
            input_string = input("텍스트 입력: ")
            split_strings = input_string.split('[>*}')
            for string in split_strings:
                if string.strip():
                    call_api(string.strip())
        except:
            exit(1)