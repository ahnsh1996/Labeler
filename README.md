# Labeler
Labeler 프로젝트의 안드로이드 앱입니다.

## Labeler 프로젝트
크라우드 소싱(Crowdsourcing) 방식을 이용하여 AI에 관한 전문지식이 없는 일반인들 역시 데이터의 수집 및 가공에 쉽게 접근할 수 있게 함으로써 AI 프로그램 개발에 필요한 데이터셋을 좀 더 쉽고 빠르게 제작할 수 있도록 하는 플랫폼을 제작하는 것

## Labeler 기능
### 이미지 수집  
이미지 수집 기능은 의뢰인이 수집하고자하는 종류의 사진을 업로드하는 기능이다. 이미지 수집 프로젝트 완료 후, 의뢰인은 수집된 이미지들의 압축 파일을 다운로드할 수 있다.  
|수행자|의뢰인|
|:---:|:---:|
|<img src="https://user-images.githubusercontent.com/77680436/105361151-bfc3b780-5c3c-11eb-865c-9530877eb6bc.gif" height="633px" width="300px">|<img src="https://github.com/ahnsh1996/Img/raw/master/Labeler/%EC%9D%B4%EB%AF%B8%EC%A7%80%20%EC%88%98%EC%A7%91%20%EA%B2%B0%EA%B3%BC.gif" height="633px" width="300px">|  

### 이미지 분류  
이미지 분류 기능은 의뢰인이 업로드한 사진들을 객체의 종류에 따라 분류하는 기능이다. 이미지 분류 프로젝트 완료 후, 의뢰인은 각 객체 이름으로 된 폴더에 분류되어 있는 사진들을 다운로드할 수 있다.  
|수행자|의뢰인|
|:---:|:---:|
|<img src="https://github.com/ahnsh1996/Img/raw/master/Labeler/%EC%9D%B4%EB%AF%B8%EC%A7%80%20%EB%B6%84%EB%A5%98.gif" height="633px" width="300px">|<img src="https://github.com/ahnsh1996/Img/raw/master/Labeler/%EC%9D%B4%EB%AF%B8%EC%A7%80%20%EB%B6%84%EB%A5%98%20%EA%B2%B0%EA%B3%BC.gif" height="633px" width="300px"></p>|  
  
### 이미지 바운딩 박스
이미지 바운딩 박스 기능은 의뢰인이 업로드한 사진에서 객체가 어느 영역에 존재하는지 라벨링하는 기능이다.</br>
객체의 영역 정보 기록 방식은   

* (min x, min y, max x, max y)  
* (center x, center y, width, height)[Yolo]   

두 가지 포맷 중 의뢰인이 원하는 것으로 선택할 수 있다.  
이미지 바운딩 박스 프로젝트 완료 후, 의뢰인은 이미지 파일과 함께 객체 정보와 바운딩 박스의 좌표 정보를 다운로드할 수 있다.
|수행자|의뢰인|
|:---:|:---:|
|<img src="https://github.com/ahnsh1996/Img/raw/master/Labeler/%EB%B0%94%EC%9A%B4%EB%94%A9%20%EB%B0%95%EC%8A%A4.gif" height="633px" width="300px">|<img src="https://github.com/ahnsh1996/Img/raw/master/Labeler/%EB%B0%94%EC%9A%B4%EB%94%A9%20%EB%B0%95%EC%8A%A4%20%EA%B2%B0%EA%B3%BC.gif" height="633px" width="300px">|  
  
### 텍스트 분류
텍스트 분류 기능은 의뢰자가 분류하고자 하는 텍스트들을 종류에 맞게 분류하는 기능이다. 텍스트 분류 프로젝트 완료 후, 의뢰인은 결과인 CSV 파일을 다운로드할 수 있다.
|수행자|의뢰인|
|:---:|:---:|
|<img src="https://github.com/ahnsh1996/Img/raw/master/Labeler/%ED%85%8D%EC%8A%A4%ED%8A%B8%20%EB%B6%84%EB%A5%98.gif" height="633px" width="300px">|<img src="https://github.com/ahnsh1996/Img/raw/master/Labeler/%ED%85%8D%EC%8A%A4%ED%8A%B8%20%EB%B6%84%EB%A5%98%20%EA%B2%B0%EA%B3%BC.gif" height="633px" width="300px">|  

## Labeler 서버
Labeler 프로젝트에 대한 서버의 경우 [이곳](https://github.com/sioni322/Node.js)에서 확인할 수 있다.
