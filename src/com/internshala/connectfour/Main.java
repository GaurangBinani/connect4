package com.internshala.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {


private Controller controller;
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader= new FXMLLoader(getClass().getResource("game.fxml"));
		GridPane rootGridPane= loader.load();

		controller= loader.getController();
		controller.createPlayground();


		createMenu();
		MenuBar menuBar= createMenu();
		Pane menuPane= (Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().add(0,menuBar);
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

		Scene scene = new Scene(rootGridPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Connect Four Game");
		primaryStage.setResizable(false);
		primaryStage.show();

	}

	private MenuBar createMenu() {
		Menu fileMenu= new Menu("File");
		MenuItem newGame= new MenuItem("New Game");
		newGame.setOnAction(event -> controller.resetgame());
		MenuItem resetGame= new MenuItem("Reset Game");
		resetGame.setOnAction(event -> controller.resetgame());
		SeparatorMenuItem separatorMenuItem= new SeparatorMenuItem();
		MenuItem exitGame= new MenuItem("Exit Game");
		exitGame.setOnAction(event -> Platform.exit());
		fileMenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);


		Menu helpmenu= new Menu("Help");
		MenuItem aboutGame= new MenuItem("About Game");
		aboutGame.setOnAction(event -> aboutGame());
		MenuItem aboutMe= new MenuItem("About Me");
		aboutMe.setOnAction(event -> aboutMe());
		helpmenu.getItems().addAll(aboutGame,aboutMe);

		MenuBar menuBar= new MenuBar();
		menuBar.getMenus().addAll(fileMenu,helpmenu);
		return menuBar;
	}

	private void aboutGame() {
		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About the Game");
		alert.setHeaderText("Connect4 Game");
		alert.setContentText("The pieces fall straight down, occupying the lowest available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own tokens. \n"  +
				"Connect Four is a solved game. The first player can always win by playing the right moves.");
		alert.show();
	}

	private void aboutMe() {
		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About the Developer");
		alert.setHeaderText("Gaurang Binani");
		alert.setContentText("Hey everybody out there this is Gaurang Binani , developer of this Connect Four game Application, hope you like it, let me know your favourite moments wiht this game through your lovely feedback");
		alert.show();
	}



	public static void main(String[]
	args) {
		launch(args);
	}
}
