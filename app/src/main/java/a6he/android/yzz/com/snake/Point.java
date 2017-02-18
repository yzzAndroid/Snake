package a6he.android.yzz.com.snake;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/2/18 0018.
 */
class Point implements Parcelable {
    float x;
    float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }


    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel in) {
            float x = in.readFloat();
            float y = in.readFloat();
            Point point = new Point(x,y);
            return point;
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
