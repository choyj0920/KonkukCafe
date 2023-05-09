from selenium import webdriver
from selenium.webdriver.common.keys import Keys
import time
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager #크롬에서 크롤링 진행 크롬 웹 드라이버 설치시 필요`
from selenium.webdriver.support import expected_conditions as EC #브라우저에 특정 요소 상태 확인을 위해
from bs4 import BeautifulSoup
from selenium.webdriver.support.ui import WebDriverWait #Explicitly wait을 위함
from selenium.common.exceptions import NoSuchElementException,StaleElementReferenceException,TimeoutException #예외처리를 위한 예외들 
import csv
import pandas as pd
import openpyxl
driver = webdriver.Chrome(ChromeDriverManager().install())
driver.get("https://map.naver.com/v5/search")
try:
   element = WebDriverWait(driver, 25).until(
       EC.presence_of_element_located((By.CLASS_NAME, "input_search"))
   ) #입력창이 뜰 때까지 대기
finally:
   pass


#찾아야하는 태그 목록들
resultListClass = "UEzoS"
#resultListTitleClass = "tzwk0"
resultListTitleClass = "P7gyV"
resultTargetTitleDiv = "YouOG"
#resultTitleClick = "#_pcmap_list_scroll_container > ul > li > div.CHC5F > a"
resultTitleClick = "#_pcmap_list_scroll_container > ul > li > div > div > a"
itemContainer="#_pcmap_list_scroll_container"
searchFrame = "searchIframe"
entryFrame = "entryIframe"
addressSpan = "IH7VW"
daySpan = "kGc0c"
timeDiv = "qo7A2"
adrSpan="LDgIH"

telSpan = "xlx7Q"
descSpan = "zPfVt"
reviewOpenerA = "xHaT3"
reviewSpan = "zPfVt"
ratingSvg = "GWkzU"
imgDiv = "K0PDV"
subpageTitle="Fc1rA"
timeOpenerSelector = "#app-root > div > div > div > div:nth-child(6) > div > div.place_section.no_margin.vKA6F > div > ul > li.SF_Mq.Sg7qM > div > a"
descOpenerSelector = "#app-root > div > div > div > div > div > div.place_section.no_margin.vKA6F > div > ul > li.SF_Mq.I5Ypx > div > a"

cafedict={}

def getInfo():
    time.sleep(2)
    
    
    html = driver.page_source
    soup = BeautifulSoup(html,'html.parser')
    
  
    strname=soup.find("span",subpageTitle).text
    print(strname)

    #전화번호 가져오기
    telStr=""
    tel= soup.find_all("span",telSpan)
    for i in range(len(tel)):
        telStr =  tel[i].text+".||."
    print("tele : "+telStr)
    #소개 가져오기
    desc = soup.find_all("span",descSpan)
    descStr=""
    for i in range(len(desc)):
        for br in desc[i].find_all("br"):
            br.replace_with(".||.")
        descStr = descStr + desc[i].text+".||."
    print("describe : "+descStr)
    address=soup.find_all("span",adrSpan)
    adrstr=""
    for i in range(len(address)):
        adrstr = adrstr + address[i].text+".||."
    print(adrstr)
    
        
    #리뷰탭으로 이동
    menuIndex = len(soup.find_all("a", "_tab-menu"))
    reviewTabOpenerSelector = "#app-root > div > div > div > div.place_fixed_maintab > div > div > div > div > a:nth-child("+str(menuIndex-1)+")"
    #reviewTabOpenerSelector = "#app-root > div > div > div > div.place_fixed_maintab.place_stuck.place_tab_shadow > div > div > div > div > a:nth-child(
    
    #app-root > div > div > div > div:nth-child(7) > div:nth-child(3) > div.place_section.lcndr > div.lfH3O > a
    try:
        #driver.find_element(By.CSS_SELECTOR,reviewTabOpenerSelector).click()
        driver.find_element(By.CSS_SELECTOR,reviewTabOpenerSelector).send_keys(Keys.ENTER)
        time.sleep(1)

        WebDriverWait(driver,2).until(
            EC.presence_of_element_located((By.CLASS_NAME, reviewSpan))
        )
        reviewOpeners = driver.find_elements(By.CLASS_NAME,reviewOpenerA)
        for opener in reviewOpeners:
            #opener.click()

            try:
                opener.send_keys(Keys.ENTER)
            except:
                pass

        html = driver.page_source
        soup = BeautifulSoup(html,'html.parser')
        reviews = soup.find_all("span",reviewSpan)
    except (TimeoutException, NoSuchElementException):
        reviews = []
    finally:
        html = driver.page_source
        soup = BeautifulSoup(html,'html.parser')
    reviewStr=""
    for i in range(len(reviews)):
        #newreview = reviews[i].text.replace("\n", "")
        reviewStr = reviewStr+ newreview +"[>*}"
        
    try:
        ratingStr = soup.find("svg",ratingSvg).next_sibling.next_sibling.text
    except:
        pass
    imgUrl = soup.find("div",imgDiv)
    imagstr=""
    try:
        imagstr = imgUrl.attrs["style"].split('"')[1]
    except AttributeError:
        imagstr = ""
    
    print("image "+imagstr)
    print(reviewStr)
    cafedict[strname]={"name":strname,"adr":adrstr,"phone":telStr,"review":reviewStr}


    time.sleep(5)
   

# 검색창에 검색어 입력하기
search_box = driver.find_element(By.CLASS_NAME,"input_search")
search_box.send_keys("광진구 화양동 카페")
search_box.send_keys(Keys.ENTER)

time.sleep(3)

# 검색버튼 누르기

driver.switch_to.default_content()
driver.switch_to.frame(searchFrame)

# 스크롤 높이 가져옴
last_height = driver.execute_script("return document.body.scrollHeight")

itemlist = driver.find_element(By.CSS_SELECTOR,itemContainer)

cnt=0
while cnt<10:
    # 끝까지 스크롤 내리기
    driver.execute_script("arguments[0].scrollBy(0, 10000)",itemlist)
    # 대기
    time.sleep(1)
    cnt+=1



cafelist= driver.find_elements(By.CSS_SELECTOR,resultTitleClick)
print(cafelist)



# 크롤링
for cafepage in cafelist:
    # 5초 delay
    
    cafepage.send_keys(Keys.ENTER)
    driver.switch_to.default_content()
    WebDriverWait(driver,4).until(
    EC.presence_of_element_located((By.ID, entryFrame))
    )
    driver.switch_to.frame(entryFrame)

    getInfo()
    driver.switch_to.default_content()
    driver.switch_to.frame(searchFrame)


    
print(cafedict)
print(len(cafedict))

field_names=["name","adr","phone","review"]



# with open('cafeinfo.csv', 'w',encoding='utf-8') as csvfile:
#     writer = csv.DictWriter(csvfile, fieldnames=field_names)
#     for i in cafedict.values():
#         writer.writerow(i)

df =pd.DataFrame([list(value.values()) for key,value in cafedict.items()],
                 index=False,columns=field_names)

with pd.ExcelWriter('./화양동카페정보.xlsx') as writer:
    df.to_excel(writer, sheet_name='sheet1')
