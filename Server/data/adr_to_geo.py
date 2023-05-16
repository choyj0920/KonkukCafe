import requests, json

def get_location(address):
  url = 'https://dapi.kakao.com/v2/local/search/address.json?query=' + address
  # 'KaKaoAK '는 그대로 두시고 개인키만 지우고 입력해 주세요.
  # ex) KakaoAK 6af8d4826f0e56c54bc794fa8a294
  headers = {"Authorization": "KakaoAK 1bc2840050fb84fd7d674719a53d85d4"}
  api_json = json.loads(str(requests.get(url,headers=headers).text))
  address = api_json['documents'][0]['address']
  crd = {"lat": str(address['y']), "lng": str(address['x'])}
  address_name = address['address_name']

  return crd

import pandas as pd

# xlsx 파일 경로
xlsx_file = '화양동카페감정평균.xlsx'

# xlsx 파일을 데이터프레임으로 읽기
df = pd.read_excel(xlsx_file)
lastdict= df.to_dict(orient='records')
newdict={}
for value in lastdict:
  try: 
    geo=get_location(value["adr"])
    del value['Unnamed: 0']
    value["lat"]=geo["lat"]
    value["lng"]=geo["lng"]

    newdict[value["name"]]=value


  except Exception as e:
    print(value)
    print("에러가 발생했습니다:", str(e))
    continue
  
  

df_geo = pd.DataFrame.from_dict(newdict, orient='index')

with pd.ExcelWriter('./화양동카페감정평균_addlocation.xlsx') as writer:
    df_geo.to_excel(writer, sheet_name='sheet1')


# 데이터프레임 확인
print(newdict)
print(len(lastdict))

print(len(newdict))