
from cafe_crawler import crawling
from text_to_emotion import call_api,start_tokenThread,finish_tokenThread
import pandas as pd
import openpyxl

probIndex=["분노", "싫음", "두려움", "행복", "슬픔", "놀람", "중립"]

class EmotionResult:
    def __init__(self,data):
        self.result = data['result']
        self.probs =data['probs']
    
    
    @staticmethod
    def getEmotionFromProb(probs):
        

        return probIndex[probs.index(max(probs))]
        
    @staticmethod
    def calculate_average_probs(results):
        num_results = len(results)
        total_probs = [0.0] * len(results[0].probs)  # 초기화
        
        for result in results:
            for i, prob in enumerate(result.probs):
                total_probs[i] += prob
        
        average_probs = [prob / num_results for prob in total_probs]
        return average_probs

if __name__ == "__main__":

    cafedict= crawling("광진구 화양동 카페")
    start_tokenThread()
    savedict={}

    for key,value in cafedict.items():
        curreviewList = list(filter(None, value["review"].split('[>*}')))
        emotionlist=[]
        try:
            for r in curreviewList:
                res = call_api(inputText=r)
                if res:
                    emotionlist.append(EmotionResult(data=res))
            if len(emotionlist)==0:
                continue

            avgresult=EmotionResult.calculate_average_probs(emotionlist)
            avgemotion=EmotionResult.getEmotionFromProb(avgresult)
            print("[",key,"] 감정분석결과 :", avgemotion,avgresult)
            value["리뷰감정"]= avgemotion
            for i in range(len(probIndex)):
                value[probIndex[i]]=avgresult[i]
            savedict[key]=value
        except Exception as e:
            print("에러가 발생했습니다:", str(e))
            continue
        


    df = pd.DataFrame.from_dict(savedict, orient='index')
    
    df_removed=df.drop(["review"],axis=1)


    with pd.ExcelWriter('./화양동카페감정평균.xlsx') as writer:
        df_removed.to_excel(writer, sheet_name='sheet1')


    # with pd.ExcelWriter('./화양동카페감정평균_리뷰추가.xlsx') as writer:
    #     df_removed.to_excel(writer, sheet_name='sheet1')
        

    finish_tokenThread()

    