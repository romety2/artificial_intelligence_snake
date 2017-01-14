package com.nanovash.snakenn.view;

import com.nanovash.snakenn.game.Direction;
import com.nanovash.snakenn.game.GameModel;
import com.nanovash.snakenn.game.Location;
import com.nanovash.snakenn.game.util.LossException;
import com.nanovash.snakenn.game.util.State;
import com.nanovash.snakenn.neuralnetwork.NNGenetics;
import com.nanovash.snakenn.neuralnetwork.NeuralNetwork;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainController implements Initializable
{

    @FXML GridPane gridPaneBoard;
    @FXML Button buttonPlay;
    @FXML Label labelScore;
    @FXML ChoiceBox players;
    @FXML TextField input;
    @FXML Button loadInput;
    @FXML Button openFolder;

    GameModel gm = new GameModel();
    int speed = 10;
    Timeline tl;
    Timeline monitor = new Timeline();
    int lastScore = 0;
    int score = 0;
    String currentPlayer = "Gra";
    NNGenetics nng = new NNGenetics();
    NeuralNetwork nn = new NeuralNetwork();

    public void initialize(URL location, ResourceBundle resources)
    {
        initChoiceBoxes();
        gm.setWalls(true);
        for (int i = 0; i < GameModel.SIDE; i++)
        {
            for (int j = 0; j < GameModel.SIDE; j++)
            {
                Rectangle r = new Rectangle(40, 40);
                r.setFill(Color.WHITE);
                r.setStroke(Color.LIGHTGREY);
                gridPaneBoard.add(r, i, j);
            }
        }
        loadStart();
    }

    public void loadStart()
    {
        score = 0;
        clear();
        HashMap<Location, State> start = gm.start();
        for (Location add : start.keySet())
            addToBoard(add, start.get(add));
    }

    public void loadUpdate()
    {
        HashMap<Location, State> updated = gm.update();
        if(updated == null)
        {
            lastScore = 0;
            buttonPlay.setText("Nowa gra");
            buttonPlay.setPrefWidth(100);
            tl.stop();
            nng.updateFitnessOfCurrent(score);
            if(currentPlayer.equals("Trening"))
            {
                monitor.stop();
                buttonPlay.fire();
            }
            return;
        }
        clear();
        score = 0;
        for (Location l : updated.keySet())
            addToBoard(l, updated.get(l));

        if(currentPlayer.equals("Gra"))
            return;
        Direction[] directions = new Direction[] {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
        Direction bestDirection = Direction.UP;
        NeuralNetwork decider = currentPlayer.equals("Trening") ? nng.getCurrent() : nn;
        double bestEval = -10;
        for (Direction direction : directions)
        {
            if(direction.isOpposite(gm.getDirection()))
                continue;
            Location present = gm.getSnake().getLocation();
            double front = -2;
            Location inFront = null;
            try
            {
                inFront = new Location(present.getX() + direction.getX(), present.getY() + direction.getY());
            } catch (LossException e)
            {
                front = -1;
            }
            if(front == -2)
                front = stateToDouble(updated.get(inFront));
            Direction relativeLeft = Direction.getRelativeLeft(direction);
            double left = -2;
            Location toLeft = null;
            try
            {
                toLeft = new Location(present.getX() + relativeLeft.getX(), present.getY() + relativeLeft.getY());
            } catch (LossException e)
            {
                left = -1;
            }
            if(left == -2)
                left = stateToDouble(updated.get(toLeft));
            if(direction.equals(Direction.getRelativeLeft(gm.getDirection())) && !gm.getSnake().getTail().isEmpty())
                left = 0;
            Direction relativeRight = Direction.getRelativeRight(direction);
            double right = -2;
            Location toRight = null;
            try
            {
                toRight = new Location(present.getX() + relativeRight.getX(), present.getY() + relativeRight.getY());
            } catch (LossException e)
            {
                right = -1;
            }
            if(right == -2)
                right = stateToDouble(updated.get(toRight));
            if(direction.equals(Direction.getRelativeRight(gm.getDirection())) && !gm.getSnake().getTail().isEmpty())
                right = 0;
            double delta = Math.sqrt(Math.pow(present.getX() - gm.getFood().getX(), 2) + Math.pow(present.getY() - gm.getFood().getY(), 2)) - Math.sqrt(Math.pow(present.getX() + direction.getX() - gm.getFood().getX(), 2) + Math.pow(present.getY() + direction.getY() - gm.getFood().getY(), 2));
            double eval = decider.calcOutput(new double[] {delta, left, front, right});
            if(eval > bestEval)
            {
                bestEval = eval;
                bestDirection = direction;
            }
            decider.reset();
        }
        gm.setPendingDirection(bestDirection);
    }

    private double stateToDouble(State state)
    {
        if(state == null)
            return 0;
        switch (state)
        {
            case FOOD:
                return 1;
            case TAIL:
                return -1;
        }
        return 0;
    }

    public void addToBoard(Location location, State state)
    {
        for (Node node : gridPaneBoard.getChildren())
            if (GridPane.getColumnIndex(node) == location.getX() && GridPane.getRowIndex(node) == location.getY())
            {
                Rectangle rectangle = ((Rectangle) node);
                switch(state)
                {
                    case HEAD:
                        rectangle.setFill(Color.DARKGREEN);
                        break;
                    case TAIL:
                        rectangle.setFill(Color.GREEN);
                        score++;
                        break;
                    case FOOD:
                        rectangle.setFill(Color.RED);
                        break;
                }
                labelScore.setText("Wynik: " + score);
            }
    }

    public void clear()
    {
        for (Node node : gridPaneBoard.getChildren())
            ((Rectangle) node).setFill(Color.WHITE);
    }

    @FXML
    public void buttonPressed()
    {
        if(buttonPlay.getText().equals("Start"))
        {
            gm.setPendingDirection(Direction.RIGHT);
            buttonPlay.getScene().setOnKeyPressed(event1 ->
            {
                if(currentPlayer.equals("Gra"))
                {
                    KeyCode code = event1.getCode();
                    if ((code.equals(KeyCode.UP) || code.equals(KeyCode.W)))
                        gm.setPendingDirection(Direction.UP);
                    else if ((code.equals(KeyCode.DOWN) || code.equals(KeyCode.S)))
                        gm.setPendingDirection(Direction.DOWN);
                    else if ((code.equals(KeyCode.LEFT) || code.equals(KeyCode.A)))
                        gm.setPendingDirection(Direction.LEFT);
                    else if ((code.equals(KeyCode.RIGHT) || code.equals(KeyCode.D)))
                        gm.setPendingDirection(Direction.RIGHT);
                }
            });
            buttonPlay.getScene().setOnMouseClicked(event ->
            {
                gridPaneBoard.requestFocus();
            });
            tl.play();
            if(currentPlayer.equals("Trening"))
                monitor.play();
        }
        else if(buttonPlay.getText().equals("Nowa gra"))
        {
            loadStart();
            tl.play();
            gm.setPendingDirection(Direction.UP);
            gm.setPendingDirection(Direction.RIGHT);
            if(currentPlayer.equals("Trening"))
                monitor.play();
        }
    }

    @FXML
    public void loadNetwork()
    {
        NeuralNetwork nn2;
        try
        {
            nn2 = new NeuralNetwork(nng.stringToList(input.getText()));
            nn = nn2;
            players.getSelectionModel().select(1);
        }
        catch(IndexOutOfBoundsException | NumberFormatException e)
        {
        }
}

    @FXML
    public void openFolder()
    {
        try
        {
            Desktop.getDesktop().open(nng.getStorePopulation().getParentFile());
        }
        catch (IOException e)
        {
        }
    }

    public void initChoiceBoxes()
    {
        players.getItems().addAll("Gra", "Symulacja", "Trening");
        players.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            switch (newValue.toString())
            {
                case "Gra":
                    speed = 10;
                    break;
                case "Symulacja":
                    speed = 10;
                    break;
                case "Trening":
                    speed = 100;
            }
            if(tl != null)
                tl.stop();
            tl = new Timeline(new KeyFrame(Duration.millis(1000 / speed), ae -> loadUpdate()));
            tl.setCycleCount(Animation.INDEFINITE);
            currentPlayer = newValue.toString();
            initMonitor();
        });
        players.getSelectionModel().select(0);
    }

    public void initMonitor()
    {
        monitor.stop();
        monitor = new Timeline(new KeyFrame(Duration.millis(100000 / speed), ae ->
        {
            if(lastScore >= score)
            {
                nng.updateFitnessOfCurrent(score);
                loadStart();
            }
            lastScore = score;
        }));
        monitor.setCycleCount(Animation.INDEFINITE);
        if(currentPlayer.equals("Trening"))
            monitor.play();
    }
}