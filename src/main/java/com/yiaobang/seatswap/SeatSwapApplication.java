package com.yiaobang.seatswap;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

public class SeatSwapApplication extends Application {

    private final List<String> allStudents = new ArrayList<>();
    private Set<String> specialStudents = new HashSet<>();
    private Map<String, Integer> specialRows = new HashMap<>();
    private Map<String, Integer> specialCols = new HashMap<>();

    private final SeatSwapEngine engine = new SeatSwapEngine();

    private String totalFileName = null;
    private String specialFileName = null;
    private String[][] currentLayout = null;
    private int currentRows = 5;
    private int currentCols = 6;

    private Locale currentLocale = Locale.JAPANESE;
    private ResourceBundle bundle;

    private Stage primaryStage;
    private Label lblLang, lblGridConfig, lblRows, lblCols, lblFileConfig, lblTotalStatus, lblSpecialStatus, lblPodium, lblTips;
    private Button btnImportTotal, btnImportSpecial, btnRun;
    private TextField txtRows, txtCols;
    private GridPane seatGrid;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png")));
            primaryStage.getIcons().add(icon);
        } catch (Exception ignored) {}
        
        GridPane rootGrid = new GridPane();
        rootGrid.setStyle("-fx-background-color: #ffffff;");

        ColumnConstraints colLeft = new ColumnConstraints(); colLeft.setPercentWidth(24.0);
        ColumnConstraints colRight = new ColumnConstraints(); colRight.setPercentWidth(76.0);
        rootGrid.getColumnConstraints().addAll(colLeft, colRight);

        RowConstraints rowConstraints = new RowConstraints(); rowConstraints.setVgrow(Priority.ALWAYS);
        rootGrid.getRowConstraints().add(rowConstraints);

        // ================= 左侧控制面板 =================
        VBox leftPane = new VBox(16.0);
        leftPane.setPadding(new Insets(24.0, 20.0, 24.0, 24.0));
        leftPane.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-width: 0 1px 0 0;");
        leftPane.setAlignment(Pos.TOP_LEFT);

        lblLang = new Label();
        lblLang.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155; -fx-font-size: 13px;");

        ComboBox<String> langSelector = new ComboBox<>();
        langSelector.getItems().addAll("日本語", "中文", "English");
        langSelector.setValue("日本語");
        langSelector.setMaxWidth(Double.MAX_VALUE);
        langSelector.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-width: 1px; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-font-size: 13px; -fx-text-fill: #1e293b; -fx-cursor: hand; -fx-padding: 2px 4px;");
        langSelector.setOnAction(_ -> {
            currentLocale = switch (langSelector.getValue()) {
                case "中文" -> Locale.CHINESE;
                case "English" -> Locale.ENGLISH;
                default -> Locale.JAPANESE;
            };
            updateTexts();
        });

        VBox langBox = new VBox(8.0, lblLang, langSelector);
        lblGridConfig = new Label();
        lblGridConfig.setStyle("-fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-font-size: 15px;");

        String inputStyle = "-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-width: 1px; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 6px 10px; -fx-font-size: 13px; -fx-text-fill: #1e293b; -fx-pref-width: 80px;";

        lblRows = new Label(); lblRows.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px; -fx-pref-width: 65px;");
        txtRows = new TextField("5"); txtRows.setStyle(inputStyle);
        HBox hBoxRows = new HBox(10.0, lblRows, txtRows); hBoxRows.setAlignment(Pos.CENTER_LEFT);

        lblCols = new Label(); lblCols.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px; -fx-pref-width: 65px;");
        txtCols = new TextField("6"); txtCols.setStyle(inputStyle);
        HBox hBoxCols = new HBox(10.0, lblCols, txtCols); hBoxCols.setAlignment(Pos.CENTER_LEFT);

        VBox gridConfigBox = new VBox(12.0, hBoxRows, hBoxCols);
        gridConfigBox.setPadding(new Insets(0, 0, 0, 4));

        lblFileConfig = new Label(); lblFileConfig.setStyle("-fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-font-size: 15px;");

        String btnBaseStyle = "-fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 9px; -fx-background-radius: 6px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 4, 0, 0, 2);";

        btnImportTotal = new Button(); btnImportTotal.setMaxWidth(Double.MAX_VALUE); btnImportTotal.setStyle("-fx-background-color: #4f46e5; " + btnBaseStyle);
        lblTotalStatus = new Label(); lblTotalStatus.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        btnImportSpecial = new Button(); btnImportSpecial.setMaxWidth(Double.MAX_VALUE); btnImportSpecial.setStyle("-fx-background-color: #0284c7; " + btnBaseStyle);
        lblSpecialStatus = new Label(); lblSpecialStatus.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        leftPane.getChildren().addAll(langBox, new Separator(), lblGridConfig, gridConfigBox, new Separator(), lblFileConfig, btnImportTotal, lblTotalStatus, btnImportSpecial, lblSpecialStatus);

        // ================= 右侧座位地图区 =================
        VBox rightPane = new VBox(15.0);
        rightPane.setPadding(new Insets(24.0)); rightPane.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(rightPane, Priority.ALWAYS);

        lblPodium = new Label(); lblPodium.setAlignment(Pos.CENTER); lblPodium.setMaxWidth(Double.MAX_VALUE);
        lblPodium.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 6, 0, 0, 3);");

        seatGrid = new GridPane(); seatGrid.setHgap(14.0); seatGrid.setVgap(14.0); seatGrid.setAlignment(Pos.CENTER); seatGrid.setPadding(new Insets(10.0));
        VBox.setVgrow(seatGrid, Priority.ALWAYS); seatGrid.setMaxWidth(Double.MAX_VALUE);

        btnRun = new Button(); btnRun.setPrefWidth(240.0); btnRun.setPrefHeight(44.0);
        btnRun.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(16, 185, 129, 0.4), 8, 0, 0, 4);");

        lblTips = new Label(); lblTips.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px; -fx-wrap-text: true; -fx-line-spacing: 0.3em;");

        rightPane.getChildren().addAll(lblPodium, seatGrid, btnRun, lblTips);
        rootGrid.add(leftPane, 0, 0); rootGrid.add(rightPane, 1, 0);

        btnImportTotal.setOnAction(_ -> loadTotalFile());
        btnImportSpecial.setOnAction(_ -> loadSpecialFile());
        btnRun.setOnAction(_ -> runAlgorithm());

        updateTexts();
        primaryStage.setScene(new Scene(rootGrid, 1150.0, 680.0));
        primaryStage.show();
    }

    // ================== 核心控制流：现在全部面向 Key 编程 ==================
    private void runAlgorithm() {
        try {
            currentRows = Integer.parseInt(txtRows.getText());
            currentCols = Integer.parseInt(txtCols.getText());
        } catch (NumberFormatException ex) {
            currentRows = 5; currentCols = 6;
        }
        int totalCapacity = currentRows * currentCols;

        // 1. 拦截：名单为空
        if (allStudents.isEmpty()) {
            showPropertyAlert(Alert.AlertType.WARNING, "alert.warn.nototal");
            return;
        }
        
        // 2. 拦截：人数超限 (带动态计算的参数传进去)
        if (allStudents.size() > totalCapacity) {
            showPropertyAlert(Alert.AlertType.ERROR, "alert.error.notenough", allStudents.size(), totalCapacity);
            return;
        }

        // 3. 计算
        SeatSwapEngine.EngineResult result = engine.arrangeSeats(currentRows, currentCols, allStudents, specialStudents, specialRows, specialCols);
        this.currentLayout = result.layout;

        renderGrid();

        // 4. 提示：规则冲突 (带冲突名单传进去)
        if (!result.failedSpecials.isEmpty()) {
            String namesStr = String.join(", ", result.failedSpecials);
            showPropertyAlert(Alert.AlertType.INFORMATION, "alert.info.conflict", namesStr);
        }
    }

    /**
     * 🌟 终极优雅：通过指定的 Property 前缀 Key，全自动渲染国际化美化弹窗
     * @param type 弹窗类型 (ERROR, WARNING, INFORMATION)
     * @param baseKey 资源文件里的基础 Key 前缀 (例如 "alert.warn.nototal")
     * @param args 动态填入占位符 {0}, {1} 的参数
     */
    private void showPropertyAlert(Alert.AlertType type, String baseKey, Object... args) {
        // 全自动拼装对应的 .title 和 .content
        String titleKey = baseKey + ".title";
        String contentKey = baseKey + ".content";

        // 从资源文件读取文本，没有就用 Key 本身作为兜底防止崩盘
        String title = (bundle != null && bundle.containsKey(titleKey)) ? bundle.getString(titleKey) : type.name();
        String rawContent = (bundle != null && bundle.containsKey(contentKey)) ? bundle.getString(contentKey) : "Missing text for " + contentKey;

        // 格式化文本中的占位符 {0}, {1}
        String formattedContent = rawContent;
        if (args != null && args.length > 0) {
            formattedContent = MessageFormat.format(rawContent, args);
        }

        // 丢给美化核心进行渲染
        renderCustomAlert(type, title, formattedContent);
    }

    // 负责给原生 Alert 穿上漂亮的 CSS 外衣
    private void renderCustomAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        if (primaryStage != null && !primaryStage.getIcons().isEmpty()) {
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(primaryStage.getIcons().get(0));
        }

        DialogPane dialogPane = alert.getDialogPane();
        String borderColor = switch (type) {
            case ERROR -> "#ef4444";
            case WARNING -> "#f59e0b";
            default -> "#3b82f6";
        };

        dialogPane.setStyle("-fx-background-color: #ffffff; -fx-border-color: " + borderColor + "; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #1e293b; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px;");
        
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 6px 14px;");
        }
        alert.showAndWait();
    }

    private void renderGrid() {
        seatGrid.getChildren().clear();
        seatGrid.getColumnConstraints().clear();
        seatGrid.getRowConstraints().clear();

        double colPercent = 100.0 / currentCols;
        for (int c = 0; c < currentCols; c++) {
            ColumnConstraints cc = new ColumnConstraints(); cc.setPercentWidth(colPercent);
            seatGrid.getColumnConstraints().add(cc);
        }

        double rowPercent = 100.0 / currentRows;
        for (int r = 0; r < currentRows; r++) {
            RowConstraints rc = new RowConstraints(); rc.setPercentHeight(rowPercent);
            seatGrid.getRowConstraints().add(rc);
        }

        for (int r = 0; r < currentRows; r++) {
            for (int c = 0; c < currentCols; c++) {
                String studentName = currentLayout[r][c];
                Label label = new Label();
                label.setMaxWidth(Double.MAX_VALUE); label.setMaxHeight(Double.MAX_VALUE);
                label.setAlignment(Pos.CENTER);

                if (studentName != null) {
                    String formattedName = studentName.contains("(") && studentName.contains(")") ? studentName.replace("(", "\n(") : studentName;
                    if (specialStudents.contains(studentName)) {
                        label.setText(formattedName + " ★");
                        label.setStyle("-fx-background-color: #dcfce7; -fx-border-color: #22c55e; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-text-fill: #166534; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-alignment: center; -fx-effect: dropshadow(three-pass-box, rgba(34,197,94,0.15), 4, 0, 0, 2);");
                    } else {
                        label.setText(formattedName);
                        label.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-text-fill: #1e293b; -fx-font-size: 14px; -fx-text-alignment: center; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.04), 4, 0, 0, 2);");
                    }
                } else {
                    String emptyText = (bundle != null && bundle.containsKey("seat.empty")) ? bundle.getString("seat.empty") : "Empty";
                    label.setText(emptyText);
                    label.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-text-fill: #cbd5e1;");
                }
                seatGrid.add(label, c, r);
            }
        }
    }

    private void loadTotalFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle((bundle != null && bundle.containsKey("dialog.total.title")) ? bundle.getString("dialog.total.title") : "Select File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));
        
        File rootFilePath = new File(System.getProperty("user.dir"));
        if (rootFilePath.exists() && rootFilePath.isDirectory()) chooser.setInitialDirectory(rootFilePath);

        File file = chooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                allStudents.clear();
                allStudents.addAll(StudentConfigParser.parseTotalFile(file));
                totalFileName = file.getName();
                updateTexts();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void loadSpecialFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle((bundle != null && bundle.containsKey("dialog.special.title")) ? bundle.getString("dialog.special.title") : "Select File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));
        
        File rootFilePath = new File(System.getProperty("user.dir"));
        if (rootFilePath.exists() && rootFilePath.isDirectory()) chooser.setInitialDirectory(rootFilePath);

        File file = chooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                StudentConfigParser.ParseSpecialResult result = StudentConfigParser.parseSpecialFile(file);
                this.specialStudents = result.specialStudents;
                this.specialRows = result.specialRows;
                this.specialCols = result.specialCols;
                
                specialFileName = file.getName();
                updateTexts();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void updateTexts() {
        try {
            bundle = ResourceBundle.getBundle("messages", currentLocale);
            primaryStage.setTitle(bundle.getString("app.title"));
            lblLang.setText(bundle.getString("ui.lang"));
            lblGridConfig.setText(bundle.getString("grid.config"));
            lblRows.setText(bundle.getString("grid.rows"));
            lblCols.setText(bundle.getString("grid.cols"));
            lblFileConfig.setText(bundle.getString("file.config"));
            btnImportTotal.setText(bundle.getString("btn.import.total"));
            btnImportSpecial.setText(bundle.getString("btn.import.special"));
            lblPodium.setText(bundle.getString("podium"));
            btnRun.setText(bundle.getString("btn.run"));
            lblTips.setText(bundle.getString("tips"));

            if (totalFileName == null) {
                lblTotalStatus.setText(bundle.getString("status.unloaded"));
            } else {
                lblTotalStatus.setText(MessageFormat.format(bundle.getString("status.loaded"), totalFileName, allStudents.size()));
            }

            if (specialFileName == null) {
                lblSpecialStatus.setText(bundle.getString("status.unloaded"));
            } else {
                lblSpecialStatus.setText(MessageFormat.format(bundle.getString("status.loaded"), specialFileName, specialStudents.size()));
            }

        } catch (Exception e) {
            // 兜底硬编码
            primaryStage.setTitle("SeatSwap Application");
            lblLang.setText("🌐 Language:"); lblGridConfig.setText("📐 1. Grid Configuration");
            lblRows.setText("Rows:"); lblCols.setText("Cols:"); lblFileConfig.setText("📁 2. Import Rosters");
            btnImportTotal.setText("Import Total Roster (.txt)"); btnImportSpecial.setText("Import Special Needs (.txt)");
            lblPodium.setText("【   Teacher's Desk   】"); btnRun.setText("🎲 Run Seat Swap");
            lblTips.setText("* System Error: properties file missing.");
        }
        if (currentLayout != null) renderGrid();
    }

    public static void main(String[] args) { launch(args); }
}