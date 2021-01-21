package com.example.labeler;

import java.text.DecimalFormat;

public class BoundingBox {
    int object, ImgWidth, ImgHeight;
    float xmin, ymin, xmax, ymax;
    boolean yolo; // yolo 포맷인지 여부
    DecimalFormat df;
    BoundingBox(int object, float xmin, float ymin, float xmax, float ymax, boolean yolo, int ImgWidth, int ImgHeight) {
        this.object = object;
        this.xmin = xmin; this.ymin = ymin;
        this.xmax = xmax; this.ymax = ymax;
        this.yolo = yolo;
        this.ImgWidth = ImgWidth;
        this.ImgHeight = ImgHeight;
        df = new DecimalFormat("0.000000");
    }
    public String getCenterX() {
        return df.format(((xmin + xmax)/2)/ImgWidth);
    }
    public String getCenterY() {
        return df.format(((ymin + ymax)/2)/ImgHeight);
    }
    public String getWidth() {
        return df.format((xmax - xmin)/ImgWidth);
    }
    public String getHeight() {
        return df.format((ymax - ymin)/ImgHeight);
    }
    @Override
    public String toString() {
        if(yolo) // yolo의 경우 중앙 좌표와 이미지의 너비, 높이의 상대적인 값
            return object+" "+getCenterX()+" "+getCenterY()+" "+getWidth()+" "+getHeight();
        else
            return object+" "+(int)xmin+" "+(int)ymin+" "+(int)xmax+" "+(int)ymax;
    }
}
