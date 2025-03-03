package com.example.calculator;
import com.example.calculator.R;
import org.mariuszgromada.math.mxparser.Expression;
import org.w3c.dom.Text;
import java.util.ArrayList;
import java.util.Objects;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.TypedValue;
import android.graphics.drawable.GradientDrawable;
import android.content.Context;
import com.google.android.material.color.MaterialColors;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
  private Button createButton(String label) {
    Button button = new Button(this);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        0,
        LinearLayout.LayoutParams.MATCH_PARENT,
        1
    );
    params.setMargins(5, 5, 5, 5);
    button.setLayoutParams(params);
    button.setText(label);
    if (label.equals("1/x") || label.equals("x²") || label.equals("²√x") || label.equals("+/-")) {
      button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
    }
    else{
      button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
    }
    int buttonColor;
    if (label != "=")
    {
      buttonColor = MaterialColors.getColor(button, com.google.android.material.R.attr.colorPrimaryVariant);
    }
    else
      buttonColor = MaterialColors.getColor(button, com.google.android.material.R.attr.colorSecondary);
    // Create a rounded drawable programmatically
    GradientDrawable shape = new GradientDrawable();
    shape.setShape(GradientDrawable.RECTANGLE);
    shape.setCornerRadius(10); // Set rounded corners
    shape.setColor(buttonColor); // Background color

    button.setBackground(shape);

    // Add OnClickListener
    button.setOnClickListener(v -> {
      appendToScreen(label);
    });
    // Add OnTouchListener
    button.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        GradientDrawable btnShape = (GradientDrawable) button.getBackground();
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN: // Button pressed
            if (!label.equals("=")) {
              btnShape.setColor(MaterialColors.getColor(button, com.google.android.material.R.attr.colorPrimary));
            } else {
              btnShape.setColor(MaterialColors.getColor(button, com.google.android.material.R.attr.colorSecondaryVariant));
            }
            break;
          case MotionEvent.ACTION_UP: // Button released
            btnShape.setColor(buttonColor); // Reset to original color
            break;
        }
        return false;
      }
    });
      return button;
    }



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // Force dark theme
    // Set the layout for the activity
    setContentView(R.layout.activity_main);

    TextView mc = findViewById(R.id.mc);
    TextView mr = findViewById(R.id.mr);
    TextView mPlus = findViewById(R.id.mPlus);
    TextView mMinus = findViewById(R.id.mMinus);
    TextView ms = findViewById(R.id.ms);
    TextView mDropdown = findViewById(R.id.mdropdown);
    // Set OnClickListeners
    mc.setOnClickListener(v -> onMcClicked(mc, mr, mDropdown));
    mr.setOnClickListener(v -> onMrClicked());
    mPlus.setOnClickListener(v -> onMPlusClicked(mc, mr, mDropdown));
    mMinus.setOnClickListener(v -> onMMinusClicked(mc, mr, mDropdown));
    ms.setOnClickListener(v -> onMsClicked(mc, mr, mDropdown));
    mDropdown.setOnClickListener(v -> onMDropdownClicked());

    // Add OnTouchListener for all buttons
//    setupButtonTouchListener(mc);
//    setupButtonTouchListener(mr);
//    setupButtonTouchListener(mPlus);
//    setupButtonTouchListener(mMinus);
//    setupButtonTouchListener(ms);
//    setupButtonTouchListener(mDropdown);

    String[][] buttonLabels = {
        {"%", "CE", "C", "⌫"},
        {"1/x", "x²", "²√x", "÷"},
        {"7", "8", "9", "×"},
        {"4", "5", "6", "−"},
        {"1", "2", "3", "+"},
        {"+/-", "0", ".", "="}
    };

    LinearLayout mainLayout = findViewById(R.id.buttonLayout);

    for(String[] row : buttonLabels) {
      LinearLayout rowLayout = new LinearLayout(this);
      rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          LinearLayout.LayoutParams.WRAP_CONTENT,
          1
      ));
      rowLayout.setOrientation(LinearLayout.HORIZONTAL);
      rowLayout.setWeightSum(4);  // WeightSum for each row

      for(String label : row) {
        Button button = createButton(label);
        if (label.equals("CE") || label.equals("C") || label.equals("⌫") || label.equals("÷") ||
            label.equals("×") || label.equals("−") || label.equals("+") || label.equals("%")) {
          button.setTextColor(MaterialColors.getColor(button, com.google.android.material.R.attr.colorSecondary));
        } else if (!label.equals("=")) {
          button.setTextColor(MaterialColors.getColor(button, com.google.android.material.R.attr.colorOnPrimary));
        } else {
          button.setTextColor(MaterialColors.getColor(button, com.google.android.material.R.attr.colorPrimary));
        }
        rowLayout.addView(button);
      }
      mainLayout.addView(rowLayout);
    }
  }
  // Helper method to set up touch listeners
//  private void setupButtonTouchListener(TextView button) {
//    button.setOnTOuchListener(new View.OnTouchListener() {
//      @Override
//      public boolean onTouch(View v, MotionEvent event) {
//        int currentTextColor = button.getCurrentTextColor();
//        switch (event.getAction()) {
//          case MotionEvent.ACTION_DOWN: // Button pressed
//            button.setTextColor(ContextCompat.getColor(this, R.color.grey_700)); // Change to grey_700
//            break;
//          case MotionEvent.ACTION_UP: // Button released
//            button.setTextColor(MaterialColors.getColor(button, com.google.android.material.R.attr.colorOnPrimary)); // Revert to default color
//            break;
//        }
//        return false; // Let the event propagate
//      }
//    });
//  }
  private ArrayList<String> memory = new ArrayList<>();
  private boolean isResultDisplayed = false;
  private boolean isErrorDisplayed = false;
  private boolean isMemoryDisplayed = false;
  private void appendToScreen(String value) {
    TextView screen = findViewById(R.id.screen);
    TextView answer = findViewById(R.id.answer);
    String currentText = screen.getText().toString();
    if (currentText.length() >= 17 && !(value.equals("C") || value.equals("CE") || value.equals("⌫"))) {
      return;
    }
    if (isErrorDisplayed) {
      if (value.equals("+") || value.equals("-") || value.equals("×") || value.equals("÷")) {
        String val = "0" + value;
        screen.setText(val);
      } else if (value.matches("[0-9]")){
        screen.setText(value);
      } else {
        screen.setText("0");
        updateTextSize(screen, 1);
      }
      isErrorDisplayed = false;
      isResultDisplayed = false;
      return;
    }
    if (isResultDisplayed) {
      if (value.matches("[+\\-×÷.]")) { // If operator or decimal, continue calculation
        screen.setText(currentText + value);
      } else if (value.equals("%")) { // Calculate percentage of result
        double percentageValue = Double.parseDouble(currentText) / 100;
        screen.setText(String.valueOf(percentageValue));
      } else if (value.equals("1/x")) {
        // Find the last entered number
        if (currentText.length() <= 13)
        {
          if (isResultDisplayed) {
            isResultDisplayed = false; // Continue calculation
          }
          String updatedText = applyReciprocal(currentText);
          screen.setText(updatedText);
          updateTextSize(screen, updatedText.length());
        } // Update text size
      }else if (value.matches("[0-9]")) { // Replace result with new number
        screen.setText(value);
      }else if (value.equals("²√x")) {
        if (currentText.length() <= 13) {
          String updatedText = handleSquareRoot(currentText);
          screen.setText(updatedText);
          updateTextSize(screen, updatedText.length());
        }
      }else if (value.equals("x²")) { // Handling square
        if (currentText.length() <= 13) {
          String updatedText = handleSquare(currentText);
          screen.setText(updatedText);
          updateTextSize(screen, updatedText.length());
        }
      }else{
        screen.setText("0");
        updateTextSize(screen, 1);
      }
      isResultDisplayed = false;
      isErrorDisplayed = false;
      updateTextSize(screen, screen.getText().length());
      showAnswer(screen.getText().toString(), value);
      return;
    }

    if (value.equals("%")) {
      if (!currentText.isEmpty() && Character.isDigit(currentText.charAt(currentText.length() - 1))) {
        handlePercentage(screen);
      }
    }else if (value.equals("C")) {
      screen.setText("0"); // Reset to 0
      updateTextSize(screen, 1); // Reset text size
      isResultDisplayed = false;
    } else if (value.equals("CE")) {
    if (!currentText.isEmpty()) {
        // Check if the last character is a bracket
        char lastChar = currentText.charAt(currentText.length() - 1);
        if (lastChar == ')' || lastChar == '(') {
          return; // Do nothing if the last character is a bracket
        }
        // Find the last operator position
        int lastOperatorIndex = findLastOperatorIndex(currentText);
        if (lastOperatorIndex != -1) {
          String newText = currentText.substring(0, lastOperatorIndex + 1);
          screen.setText(newText);
          updateTextSize(screen, newText.length());
        } else {
          screen.setText("0"); // If no operator, reset to 0
          updateTextSize(screen, 1);
        }
      }
    } else if (value.equals("⌫")) {
      if (!currentText.isEmpty()) {
        String newText = currentText.substring(0, currentText.length() - 1);
        screen.setText(newText.isEmpty() ? "0" : newText);
        updateTextSize(screen, newText.length());
        isResultDisplayed = false;
      }
    } else if (value.equals("=")) {
      // cal the calculateExpression function and display the result
      String formattedExpression = formatExpressionForParser(currentText);
      String result = calculateExpression(formattedExpression);
      screen.setText(result); // Show only the result
      Log.d("Result", result);
      updateTextSize(screen, result.length());
      if (result.equals("Error")) {
        isErrorDisplayed = true;
      } else {
        isResultDisplayed = true; // Update text size
      }
    } else if (value.equals("1/x")) {
      // Find the last entered number
      if (currentText.length() <= 13)
      {
        String updatedText = applyReciprocal(currentText);
        screen.setText(updatedText);
        updateTextSize(screen, updatedText.length());
      } // Update text size
    } else if (value.equals("²√x")) {
      if (currentText.length() <= 13) {
        String updatedText = handleSquareRoot(currentText);
        screen.setText(updatedText);
        updateTextSize(screen, updatedText.length());
      }
    }else if (value.equals("x²")) { // Handling square
      if (currentText.length() <= 13) {
        String updatedText = handleSquare(currentText);
        screen.setText(updatedText);
        updateTextSize(screen, updatedText.length());
      }
    }else if (value.equals("+/-")) {
      screen.setText(handleNegate(currentText));
      updateTextSize(screen, screen.getText().length());
    }else { // Number input
      if (isResultDisplayed) {
        screen.setText(value); // Start fresh if result was displayed
        isResultDisplayed = false;
      } else {
        if (currentText.equals("0")) {
          screen.setText(value);
        } else {
          screen.append(value);
        }
      }
    }
    updateTextSize(screen, screen.getText().length());
    showAnswer(screen.getText().toString(), value);
  }
  private String handleSquare(String expression) {
    if (expression.isEmpty()) return expression;

    // Find the last number in the expression
    int lastOperatorIndex = findLastOperatorIndex(expression);
    String lastNumber = expression.substring(lastOperatorIndex + 1); // Extract the last number

    if (lastNumber.isEmpty()) return expression; // If no number, return as is

    // Append ^2 to the last number
    return expression.substring(0, lastOperatorIndex + 1) + lastNumber + "^2";
  }
  private String handleSquareRoot(String expression) {
    if (expression.isEmpty()) return expression;

    int lastOperatorIndex = findLastOperatorIndex(expression);

    String lastNumber = expression.substring(lastOperatorIndex + 1);

    if (lastNumber.isEmpty()) return expression; // If no number, return as is

    // Check if the last number is already a square root
    if (lastNumber.startsWith("√(") && lastNumber.endsWith(")")) {
      String newExpression = expression.substring(0, lastOperatorIndex + 1) + "√(" + lastNumber + ")";
      return newExpression;
    } else {
      String newExpression = expression.substring(0, lastOperatorIndex + 1) + "√(" + lastNumber + ")";
      return newExpression;
    }
  }
  private void handlePercentage(TextView screen) {
    String currentText = screen.getText().toString();

    if (currentText.matches("\\d+(\\.\\d+)?")) { // Single number case
      double value = Double.parseDouble(currentText) / 100;
      screen.setText(String.valueOf(value));
      return;
    }

    int lastOperatorIndex = findLastOperatorIndex(currentText);
    if (lastOperatorIndex != -1) {
      String beforeOperator = currentText.substring(0, lastOperatorIndex);
      String lastNumber = currentText.substring(lastOperatorIndex + 1);

      boolean hasBrackets = lastNumber.startsWith("(") && lastNumber.endsWith(")");
      if (hasBrackets) {
        lastNumber = lastNumber.substring(1, lastNumber.length() - 1); // Remove brackets
      }

      String evaluatedResult = calculateExpression(formatExpressionForParser(beforeOperator));
      if (!evaluatedResult.equals("Error")) {
        double percentageValue = (Double.parseDouble(lastNumber) / 100) * Double.parseDouble(evaluatedResult);
        String pv = (percentageValue == (int) percentageValue) ? String.valueOf((int) percentageValue) : String.valueOf(percentageValue);

        if (hasBrackets) {
          pv = "(" + pv + ")"; // Re-enclose in brackets
        }

        char lastOperator = currentText.charAt(lastOperatorIndex);
        String newExpression = beforeOperator + lastOperator + pv;
        screen.setText(newExpression);
      }
    }
  }

  private int findLastOperatorIndex(String expression) {
    for (int i = expression.length() - 1; i >= 0; i--) {
      char c = expression.charAt(i);
      if (c == '+' || c == '-' || c == '×' || c == '÷') {
        return i;
      }
    }
    return -1;
  }
  private void updateTextSize(TextView screen, int length) {
    if (length <= 12) {
      screen.setTextSize(TypedValue.COMPLEX_UNIT_SP, 55); // Default size
    } else if (length > 12 && length <= 17) {
      screen.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35); // Smaller size
    }
  }
  private String formatExpressionForParser(String input) {
    return input
        .replace("×", " * ")
        .replace("÷", " / ")
        .replace("+", " + ")
        .replace("√", "sqrt")
        .replace("−", " - ");
  }
  private String calculateExpression(String input) {
    Expression expression = new Expression(input);
    double result = expression.calculate();
    Log.d("Actual Result", String.valueOf(result));
    if (Double.isNaN(result)) {
      return "Error";
    }

    String resultStr = (result == (int) result) ? String.valueOf((int) result) : String.valueOf(result);

    // If result exceeds 17 characters
    if (resultStr.length() > 17) {
      if (resultStr.contains(".") && resultStr.contains("E")) {
        // Convert to scientific notation with 10 decimal places
        String sciNotation = String.format("%.10e", result);

        int eIndex = sciNotation.indexOf('E'); // Find index of 'E'
        if (eIndex > 10) { // Ensure we keep 10 digits after the decimal
          sciNotation = sciNotation.substring(0, eIndex) + sciNotation.substring(eIndex);
        }
        return sciNotation;
      } else if (resultStr.contains(".")) {
        return resultStr.substring(0, 17); // Trim floating-point numbers
      } else {
        return String.format("%.10e", result); // Convert to scientific notation
      }
    }
    return resultStr;
  }
  private String applyReciprocal(String expression) {
    if (expression.isEmpty()) return expression;

    // Find the last number in the expression
    int lastOperatorIndex = findLastOperatorIndex(expression);

    String lastNumber = expression.substring(lastOperatorIndex + 1); // Extract the last number
    if (lastNumber.isEmpty()) return expression; // If no number, return as is

    // Replace the last number with its reciprocal
    String newExpression = expression.substring(0, lastOperatorIndex + 1) + "1/(" + lastNumber + ")";
    return newExpression;
  }
  private String handleNegate(String expression) {
    if (expression.length() <= 13) {
      if (expression.isEmpty()) return expression;

      // Find the last number in the expression
      Integer lastOperatorIndex = findLastOperatorIndex(expression);
      if (lastOperatorIndex == -1) {
        return "-(" + expression + ")";
      }
      while (expression.charAt(lastOperatorIndex + 1) == '(') {
        lastOperatorIndex++;
      }
      String lastNumber = expression.substring(lastOperatorIndex + 1).trim(); // Extract last number
      if (lastNumber.isEmpty()) return expression; // No number found, return as is

      // Apply nesting negation
      return expression.substring(0, lastOperatorIndex + 1) + "-(" + lastNumber + ")";
    }
    return expression;
  }
  private void showAnswer(String expression, String label) {

    TextView answerTextView = findViewById(R.id.answer);
    // Handle clear (C) label
    if (label.equals("C")) {
      answerTextView.setText(""); // Clear the TextView
      return;
    }
    String formattedExpression = formatExpressionForParser(expression);
    String result = calculateExpression(formattedExpression);
    if (result.equals("Error")) {
      answerTextView.setText("error"); // Display error message
      return;
    }
    answerTextView.setText(result);
  }

  //Memory function
  private void onMcClicked(TextView mc, TextView mr, TextView mDropdown) {
    updateButtonColor(mc, mr, mDropdown, false);
  }
  private void onMrClicked() {
    if (!memory.isEmpty()){
      TextView screen = findViewById(R.id.screen);
      screen.setText(memory.get(memory.size() - 1));
      isResultDisplayed = true;
    }
  }
  private void onMPlusClicked(TextView mc, TextView mr, TextView mDropdown) {
    TextView screen = findViewById(R.id.screen);
    String currentText = screen.getText().toString();
    String result = calculateExpression(formatExpressionForParser(currentText));
    if(!Objects.equals(result, "Error")){
      if (memory.isEmpty()){
        memory.add(result);
      }
      else{
        String finalResilt = calculateExpression(formatExpressionForParser(memory.get(memory.size() - 1) + "+" + result));
        memory.set(memory.size() - 1, finalResilt);
      }
      updateButtonColor(mc, mr, mDropdown, true);
      Log.d("Memory", memory.toString());
    }
  }
  private void onMMinusClicked(TextView mc, TextView mr, TextView mDropdown) {
    TextView screen = findViewById(R.id.screen);
    String currentText = screen.getText().toString();
    String result = calculateExpression(formatExpressionForParser(currentText));
    if(!Objects.equals(result, "Error")){
      if (memory.isEmpty()){
        if (result.charAt(0) == '-'){
          memory.add(result.substring(1));
        }
        else{
          memory.add("-" + result);
        }
      }
      else{
        String finalResilt = calculateExpression(formatExpressionForParser(memory.get(memory.size() - 1) + "-" + result));
        memory.set(memory.size() - 1, finalResilt);
      }
      updateButtonColor(mc, mr, mDropdown, true);
      Log.d("Memory", memory.toString());
    }
  }
  private void onMsClicked(TextView mc, TextView mr, TextView mDropdown) {
    TextView screen = findViewById(R.id.screen);
    String currentText = screen.getText().toString();
    String result = calculateExpression(formatExpressionForParser(currentText));
    if(!Objects.equals(result, "Error")){
      memory.add(result);
      Log.d("Memory", memory.toString());
      updateButtonColor(mc, mr, mDropdown, true);
    }
  }
  private void onMDropdownClicked() {
    RelativeLayout mem = findViewById(R.id.memory);
    if (isMemoryDisplayed){
      mem.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_down));
      new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
          mem.setVisibility(View.GONE);
        }
      }, 500); // 1000 milliseconds = 1 second

      isMemoryDisplayed = false;
    }
    else if(!memory.isEmpty()){
      mem.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
      mem.setVisibility(View.VISIBLE);
      isMemoryDisplayed = true;
    }
  }
  private void updateButtonColor(TextView textView1, TextView textView2, TextView textView3, boolean useColorOnPrimary) {
    int color;

    if (useColorOnPrimary) {
      // Use colorOnPrimary from the theme
      color = MaterialColors.getColor(textView1, com.google.android.material.R.attr.colorOnPrimary);
    } else {
      // Use grey_700 from resources
      color = ContextCompat.getColor(this, R.color.grey_700);
    }

    // Set the text color for all three TextViews
    textView1.setTextColor(color);
    textView2.setTextColor(color);
    textView3.setTextColor(color);
  }
}