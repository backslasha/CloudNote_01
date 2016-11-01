package mouse.com.cloudnote_01.fire;

public class Spark {
    public int color;
    public Double direction;
    public float speed;
    public float x = 0;
    public float y = 0;

    public Spark(float speed, int color, double direction) {
        this.speed = speed;
        this.color = color;
        this.direction = direction;
    }
}
