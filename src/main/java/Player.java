public class Player {
    private int height;
    private int weight;
    private double age;
    private String name;
    private String position;

    public Player(String name, String position,int height, int weight, double age) {
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.name = name;
        this.position = position;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public double getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "Player{" +
                "height=" + height +
                ", weight=" + weight +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}
