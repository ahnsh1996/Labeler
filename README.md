# Labeler
Labeler 프로젝트의 안드로이드 앱입니다.

## Labeler 프로젝트
크라우드 소싱(Crowdsourcing) 방식을 이용하여 AI에 관한 전문지식이 없는 일반인들 역시 데이터의 수집 및 가공에 쉽게 접근할 수 있게 함으로써 AI 프로그램 개발에 필요한 데이터셋을 좀 더 쉽고 빠르게 제작할 수 있도록 하는 플랫폼을 제작하는 것

## Labeler 기능
### 이미지 수집  
이미지 수집 기능은 의뢰인이 수집하고자하는 종류의 사진을 업로드하는 기능이다. 이미지 수집 프로젝트 완료 후, 의뢰인은 수집된 이미지들의 압축 파일을 다운로드할 수 있다.  
|수행자|의뢰인|
|:---:|:---:|
|![이미지 수집(수행자)](https://user-images.githubusercontent.com/77680436/105406730-2ca47500-5c70-11eb-9d85-19cfc9f7f758.gif)|![이미지 수집(의뢰인)](https://user-images.githubusercontent.com/77680436/105406762-37f7a080-5c70-11eb-823f-283964129917.gif)|  

### 이미지 분류  
이미지 분류 기능은 의뢰인이 업로드한 사진들을 객체의 종류에 따라 분류하는 기능이다. 이미지 분류 프로젝트 완료 후, 의뢰인은 각 객체 이름으로 된 폴더에 분류되어 있는 사진들을 다운로드할 수 있다.  
|수행자|의뢰인|
|:---:|:---:|
|![이미지 분류(수행자)](https://user-images.githubusercontent.com/77680436/105406799-42b23580-5c70-11eb-9831-fc29048dd830.gif)|![이미지 분류(의뢰인)](https://user-images.githubusercontent.com/77680436/105406830-4e056100-5c70-11eb-94e7-e68479dcfcdc.gif)|  
  
### 이미지 바운딩 박스
이미지 바운딩 박스 기능은 의뢰인이 업로드한 사진에서 객체가 어느 영역에 존재하는지 라벨링하는 기능이다.</br>
객체의 영역 정보 기록 방식은   

* (min x, min y, max x, max y)  
* (center x, center y, width, height)[Yolo]   

두 가지 포맷 중 의뢰인이 원하는 것으로 선택할 수 있다.  
이미지 바운딩 박스 프로젝트 완료 후, 의뢰인은 이미지 파일과 함께 객체 정보와 바운딩 박스의 좌표 정보를 다운로드할 수 있다.
|수행자|의뢰인|
|:---:|:---:|
|![이미지 바운딩 박스(수행자)](https://user-images.githubusercontent.com/77680436/105406861-58275f80-5c70-11eb-8959-db7e12c2ecb5.gif)|![이미지 바운딩 박스(의뢰인)](https://user-images.githubusercontent.com/77680436/105406897-64132180-5c70-11eb-8df7-7336d16f1823.gif)|  
  
### 텍스트 분류
텍스트 분류 기능은 의뢰자가 분류하고자 하는 텍스트들을 종류에 맞게 분류하는 기능이다. 텍스트 분류 프로젝트 완료 후, 의뢰인은 결과인 CSV 파일을 다운로드할 수 있다.
|수행자|의뢰인|
|:---:|:---:|
|![텍스트 분류(수행자)](https://user-images.githubusercontent.com/77680436/105406925-6d03f300-5c70-11eb-818b-f784ab46dfd8.gif)|![텍스트 분류(의뢰인)](https://user-images.githubusercontent.com/77680436/105406956-755c2e00-5c70-11eb-9bee-b1b08fac770b.gif)|  

## Labeler 서버
Labeler 프로젝트에 대한 서버의 경우 [이곳](https://github.com/sioni322/Node.js)에서 확인할 수 있다.
