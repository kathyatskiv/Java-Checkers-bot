public class Move{

    int from;
    int to;
    private int weight;

    Move(int from, int to){
        this.from = from;
        this.to = to;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }
    public void changeWeight(int weight){
        this.weight += weight;
    }

    public int getWeight(){
        return this.weight;
    }

    @Override
    public String toString() {
        return "[" +
                from +
                ", " + to +
                "]";
    }
}
