import java.util.*;


//Class representing a Bike Object with X Y and active (active or not
class Bike {
    private int X;
    private int Y;
    private boolean active;

    public Bike(int X, int Y, boolean active){
        this.X=X;
        this.Y=Y;
        this.active = active;
    }

    public int getX(){
        return X;
    }
    public int getY(){
        return Y;
    }
    public boolean getActive(){
        return active;
    }
    public void setX(int X){
        this.X=X;
    }
    public void setY(int Y){
        this.Y=Y;
    }
    public void setActive(boolean A){
        this.active =A;
    }

    public Bike (Bike b){
        this.X = b.X;
        this.Y = b.Y;
        this.active = b.active;
    }
}


class State{

    Bike[] bikes;
    int speed;

    public State (Bike[] bikes, int speed){
        this.bikes = bikes;
        this.speed = speed;
    }

    public State (State s){
        this.bikes = new Bike[s.getBikes().length];
        for(int i=0; i<s.getBikes().length; i++){
            this.bikes[i] = new Bike(s.getBikes()[i]);
        }
        this.speed = s.getSpeed();
    }

    public Bike[] getBikes(){
        return this.bikes;
    }

    public int getSpeed(){
        return this.speed;
    }

    public void setBikes (Bike[] bikes){
        this.bikes = bikes;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }

}



//Enum Action : Each Action implements the abstract method nextState which returns the resulting state of doing this action
enum Action {
    SPEED{
        @Override
        State nextState(State currentState, final char[][] road){
            //the resulting state
            State state = new State(currentState);
            //speed up
            state.setSpeed(currentState.getSpeed()+1);
            //for each bike moveForward
            for (Bike b : state.getBikes()){
                if (b.getActive()){
                    moveForward(road, b, state.getSpeed());
                }
            }
            return state;
        }
        //show action name
        @Override
        String show(){
            return "SPEED";
        }
    },
    JUMP{
        @Override
        State nextState(State currentState, final char[][] road){
            State state = new State(currentState);
            for (Bike b : state.getBikes()){
                if (b.getActive()){
                    jump(road, b, state.getSpeed());
                }
            }
            return state;
        }
        //show action name
        @Override
        String show(){
            return "JUMP";
        }
    },
    DOWN{
        @Override
        State nextState(State currentState, final char[][] road){
            State state = new State(currentState);
            for (Bike b : state.getBikes()){
                if (b.getActive()){
                    change(road, b, state.getBikes(), state.getSpeed(), +1);
                }
            }
            return state;
        }
        //show action name
        @Override
        String show(){
            return "DOWN";
        }
    },
    UP{
        @Override
        State nextState(State currentState, final char[][] road){
            State state = new State(currentState);
            for (Bike b : state.getBikes()){
                if (b.getActive()){
                    change(road, b, state.getBikes(), state.getSpeed(), -1);
                }
            }
            return state;
        }
        //show action name
        @Override
        String show(){
            return "UP";
        }
    },
    SLOW{
        @Override
        State nextState(State currentState, final char[][] road){
            //Slow if not at speed 1 :
            // !!!! if speed = 1, can't slow anymore, null move 
            if(currentState.getSpeed()<2){
                return null;
            }
            State state = new State(currentState);
            //speed down
            state.setSpeed(currentState.getSpeed()-1);
            for (Bike b : state.getBikes()){
                if (b.getActive()){
                    moveForward(road, b, state.getSpeed());
                }
            }
            return state;
        }
        //show action name
        @Override
        String show(){
            return "SLOW";
        }
    },
    WAIT{
        @Override
        State nextState(State currentState, final char[][] road){
            State state = new State(currentState);
            for (Bike b : state.getBikes()){
                if (b.getActive()){
                    moveForward(road, b, state.getSpeed());
                }
            }
            return state;
        }
        //show action name
        @Override
        String show(){
            return "WAIT";
        }
    };

    //Abstract methode returning resulting state after action a
    abstract State nextState(State currentState, final char[][] road);
    // to print action name
    abstract String show();
    // Number of lines on road
    static final int YMAX = 4;

    // méthode returning weather road is safe or not between x1 and x2 (destination)
    public boolean safeRoad(char[][] road, int currentX, int destX, int Y){
        for(int i=currentX; i<=destX; i++){
            if(road[Y][i]=='0'){
                return false;
            }
        }
        return true;
    }

    // méthode to move forward
    public void moveForward(char[][] road, Bike bike, int currentSpeed){
        int destX = bike.getX()+currentSpeed;
        boolean safeRoad = safeRoad(road, Math.min(bike.getX(), road[0].length-1), Math.min(destX, road[0].length-1), bike.getY());
        if(safeRoad){
            bike.setX(destX);
        } else {
            bike.setActive(false);
        }
    }

    // methode to jump
    public void jump(char[][] road, Bike bike, int currentSpeed){
        int destX = bike.getX()+currentSpeed;
        boolean safeRoad = road[bike.getY()][Math.min(destX, road[0].length-1)] != '0';
        if(safeRoad){
            bike.setX(bike.getX()+currentSpeed);
        } else {
            bike.setActive(false);
        }
    }

    //methode that return if the line we want to move into, is ocuupied by an other bike or not
    public boolean occupiedline(Bike[] bikes, int destY){
        for(int i=0;i<bikes.length;i++){
            if(bikes[i].getY()==destY && bikes[i].getActive())
                return true;
        }
        return false;
    }

    //méthode to change the line (deviation takes -1 value to UP +1 to DOWN)
    public void change(char[][] road, Bike bike, Bike[] bikes, int currentSpeed, int deviation){
        int destX = bike.getX()+currentSpeed;
        int destY = bike.getY()+deviation;
        if(destY<0 || destY>=YMAX || occupiedline(bikes, destY)){
            moveForward(road, bike, currentSpeed);
        } else {
            boolean safeRoad = safeRoad(road, Math.min(bike.getX(), road[0].length-1), destX-1, bike.getY()) &&
                    safeRoad(road, Math.min(bike.getX()+1, road[0].length-1), destX, destY);
            if(safeRoad){
                bike.setX(destX);
                bike.setY(destY);
            } else {
                bike.setActive(false);
            }
        }
    }

}


/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    //number of lines
    static final int YMAX = 4;

    //Backtrack Algorithm
    public static Action solve(State state, int V, char[][] road){
        int bikesAlive = 0;
        boolean win = false;
        for (Bike b : state.getBikes()){
            if (b.getActive()) {
                bikesAlive++;
            }
            if (b.getX()>=road[0].length) {
                win = true;
            }
        }
        //if number of bikes remaining is < than the minimum required, we return null and backtrack the node on the tree
        if (bikesAlive < V)
            return null;
        //if we won we just WAIT
        if (win)
            return Action.WAIT;

        //for Each action we call recursively the solve méthod until finding a wining solution move
        for (Action action : Action.values()) {
            State nextState = action.nextState(state, road);
            if (nextState != null && solve(nextState, V, road) != null)
                return action;
        }
        return null;
    }


    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int M = in.nextInt(); // the amount of motorbikes to control
        int V = in.nextInt(); // the minimum amount of motorbikes that must survive
        String L0 = in.next(); // L0 to L3 are lanes of the road. active dot character . represents a safe space, a zero 0 represents a hole in the road.
        String L1 = in.next();
        String L2 = in.next();
        String L3 = in.next();

        char[][] road = new char[YMAX][L0.length()];
        road[0] = L0.toCharArray();
        road[1] = L1.toCharArray();
        road[2] = L2.toCharArray();
        road[3] = L3.toCharArray();

        // game loop
        while (true) {
            Bike[] bikes = new Bike[M];
            int S = in.nextInt(); // the motorbikes' speed
            for (int i = 0; i < M; i++) {
                int X = in.nextInt(); // x coordinate of the motorbike
                int Y = in.nextInt(); // y coordinate of the motorbike
                int A = in.nextInt(); // indicates whether the motorbike is activated "1" or detroyed "0"                
                bikes[i] = new Bike(X,Y,A==1 ? true : false);
            }

            State state = new State(bikes, S);

            //calling solve méthode
            Action solution = solve(state, V, road);

            //PS : We could have calculated a list of moves outside the while(true) loop and just print them in order after each tour

            // active single line containing one of 6 keywords: SPEED, SLOW, JUMP, WAIT, UP, DOWN.
            System.out.println(solution.show());
        }
    }
}