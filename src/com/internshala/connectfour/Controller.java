package com.internshala.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable{
	@FXML
	public GridPane rootGridPane;
	@FXML
	public Pane insertedDiscPane;
	@FXML
	public Button setName;
	@FXML
	public TextField playerOneName;
	@FXML
	public TextField playerTwoName;
	@FXML
	public Label playerOneLabel;
	private static final int columns= 7;
	private static final int rows= 6;
	private static final int circle_Diameter=106;
	private static final String disc1Color="#24303E";
	private static final String disc2COlor="#4CAA88";

	private boolean p1Turn=true;

private Disc insertDiscArray[][]= new Disc[rows][columns];

	private static String playerOne="Player One";
	private static String playerTwo="Player Two";
private boolean isAllowedtoInsert=true;
	public void createPlayground(){
		setName.setOnAction(event ->
		{
			playerOne = playerOneName.getText();
			playerTwo = playerTwoName.getText();
		});

		Shape rectangle= playgroundGrid();
		rootGridPane.add(rectangle,0,1);
		List<Rectangle> rectangleList= createClickableColumns();
		for (Rectangle rectangle1:rectangleList
				) rootGridPane.add(rectangle1,0,1);

	}
	private Shape playgroundGrid(){
		Shape rectangle= new Rectangle((1+columns)*circle_Diameter,(1+rows)*circle_Diameter);
		rectangle.setFill(Color.WHITE);

		for(int i= 0;i<rows;i++){
			for(int j=0;j<columns;j++) {
				Circle circle = new Circle();
				circle.setCenterY(circle_Diameter / 2);
				circle.setRadius(circle_Diameter / 2);
				circle.setCenterX(circle_Diameter / 2);
				circle.setSmooth(true);
				circle.setTranslateX(j*(6+circle_Diameter)+circle_Diameter/3);
				circle.setTranslateY(i*(6+circle_Diameter)+circle_Diameter/3);

				rectangle= Shape.subtract(rectangle,circle);
			}
		}
		rectangle.setFill(Color.WHITE);
		return rectangle;
	}
	private List<Rectangle> createClickableColumns(){
		List<Rectangle> rectangleList= new ArrayList<>();
		for(int i=0; i<columns;i++){
			Rectangle rectangle1= new Rectangle((circle_Diameter),(1+rows)*circle_Diameter);
			rectangle1.setFill(Color.TRANSPARENT);
			rectangle1.setTranslateX(i*(6+circle_Diameter)+circle_Diameter/3);
			rectangle1.setOnMouseEntered(event -> rectangle1.setFill(Color.valueOf("#eeeeee26")));
			rectangle1.setOnMouseExited(event -> rectangle1.setFill(Color.TRANSPARENT));
			final int column= i;
			rectangle1.setOnMouseClicked(event -> {
				if(isAllowedtoInsert) {
					isAllowedtoInsert=false;
					insertDisc(new Disc(p1Turn), column);
				}
			});

			rectangleList.add(rectangle1);

		}
		return rectangleList;
	}

	private void insertDisc(Disc disc, int Column){
		int row= rows-1;
		while(row>=0){
			if(getDiscIfPresent(row,Column) == null)
			break;
			row--;
		}
		if(row<0)
			return;

	insertDiscArray[row][Column]=disc;
	insertedDiscPane.getChildren().add(disc);

	disc.setTranslateX(Column*(6+circle_Diameter)+circle_Diameter/3);
		int currentRow=row;
		TranslateTransition translateTransition= new TranslateTransition(Duration.seconds(0.2),disc);
		translateTransition.setToY(row*(6+circle_Diameter)+circle_Diameter/3);
		translateTransition.setOnFinished(event -> {
			isAllowedtoInsert=true;
			if(gameEnded(currentRow,Column)){
					gameOver();
					return;
			}
			p1Turn=!p1Turn;
			playerOneLabel.setText(p1Turn?playerOne:playerTwo);

		});
		translateTransition.play();
	}

	private void gameOver() {
		String winner= p1Turn? playerOne:playerTwo;
		System.out.println("Winner is "+ winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("The Winner Is"+ winner);
		alert.setContentText("Want to Play Again?");
		ButtonType yesBtn= new ButtonType("Yes");
		ButtonType noBtn= new ButtonType("No,Exit");

		alert.getButtonTypes().setAll(yesBtn,noBtn);
		Platform.runLater(()->{
		Optional<ButtonType>btnClicked= alert.showAndWait();
		if(btnClicked.isPresent() && btnClicked.get()==yesBtn){
			resetgame();
		}else{
			Platform.exit();
			System.exit(0);
		}
	});
	}
	public void resetgame() {
		insertedDiscPane.getChildren().clear();
		for(int row=0;row<insertDiscArray.length;row++){
			for(int column=0;column<insertDiscArray[row].length;column++){
				insertDiscArray[row][column]=null;
			}
		}
		p1Turn=true;
		playerOneLabel.setText(playerOne);



		createPlayground();


	}

	private boolean gameEnded(int Row,int Column){
		List<Point2D> verticalPoints=IntStream.rangeClosed(Row-3,Row+3)
												.mapToObj(r-> new Point2D(r,Column))
												.collect(Collectors.toList());
		List<Point2D> horizontalPoints=IntStream.rangeClosed(Column-3,Column+3)
				.mapToObj(c-> new Point2D(Row,c))
				.collect(Collectors.toList());
		Point2D startPoint1=new Point2D(Row-3, Column+3);
		List<Point2D> diagonal1Points=IntStream.rangeClosed(0,6)
				.mapToObj(i-> startPoint1.add(i,-i))
				.collect(Collectors.toList());

		Point2D startPoint2=new Point2D(Row-3, Column-3);
		List<Point2D> diagonal2Points=IntStream.rangeClosed(0,6)
				.mapToObj(i-> startPoint2.add(i,i))
				.collect(Collectors.toList());
		boolean isEnded=checkCombination(verticalPoints)||checkCombination(horizontalPoints)||checkCombination(diagonal1Points)||checkCombination(diagonal2Points);
		return isEnded;
	}

	private boolean checkCombination(List<Point2D> points) {
		int chain=0;

		for (Point2D point:points) {
			int rowIndexForArray= (int) point.getX();
			int columnIndexForArray= (int) point.getY();

			Disc disc= getDiscIfPresent(rowIndexForArray,columnIndexForArray);
			if(disc!=null && disc.p1turn==p1Turn){
				chain++;

				if(chain==4){
					return true;
				}
			}else{
				chain=0;
			}

		}

		return false;
	}
	private Disc getDiscIfPresent(int row, int column){
		if(row>=rows||row<0||column>=columns||column<0)
			return null;
		return insertDiscArray[row][column];



	}

	private static class Disc extends Circle{
		private final boolean p1turn;

		public Disc(boolean p1turn){
			this.p1turn= p1turn;
			setRadius(circle_Diameter/2);
			setCenterX(circle_Diameter/2);
			setCenterY(circle_Diameter/2);

			setFill(p1turn? Color.valueOf(disc1Color):Color.valueOf(disc2COlor));

		}

	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {


	}
}