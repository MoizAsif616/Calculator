package com.example.calculator;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

import org.mariuszgromada.math.mxparser.Expression;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.graphics.Color; // For Color
import android.util.TypedValue; // For TypedValue
import android.widget.EditText; // For EditText
import android.widget.TextView; // For TextView (if needed)
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.content.res.TypedArray;
import android.content.Context;
import androidx.core.content.ContextCompat; // For getColor()
import com.google.android.material.color.MaterialColors;
import android.widget.ImageView; // For ImageView
import android.graphics.drawable.Drawable; // If needed for setting images
import android.view.View; // For setOnClickListener
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;
import android.view.MenuItem;



public class MainActivity extends AppCompatActivity {
  private int getThemeColor(Context context, int attributeId) {
    TypedValue typedValue = new TypedValue();
    context.getTheme().resolveAttribute(attributeId, typedValue, true);
    return typedValue.data;
  }
  private Button createButton(String label) {
    Button button = new Button(this);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        0,
        LinearLayout.LayoutParams.MATCH_PARENT,
        1
    );
    params.setMargins(15, 15, 15, 15);
    button.setLayoutParams(params);
    button.setText(label);
    button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
    int buttonColor;
    if (label != "=")
    {
      buttonColor = MaterialColors.getColor(button, com.google.android.material.R.attr.colorPrimaryVariant);
    }
    else {
      buttonColor = MaterialColors.getColor(button, com.google.android.material.R.attr.colorSecondary);
    }
    // Create a rounded drawable programmatically
    GradientDrawable shape = new GradientDrawable();
    shape.setShape(GradientDrawable.RECTANGLE);
    shape.setCornerRadius(20); // Set rounded corners
    shape.setColor(buttonColor); // Background color

    button.setBackground(shape);

    // Add OnClickListener
    button.setOnClickListener(v -> {
      appendToScreen(label);
    });
    // Add OnTouchListener
    button.setOnTouchListener((v, event) -> {
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
    });
    return button;
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // Force dark theme
    // Set the layout for the activity
    setContentView(R.layout.activity_main);
//    switchTheme();

    String[][] buttonLabels = {
        {"CE", "C", "⌫", "÷"},
        {"7", "8", "9", "×"},
        {"4", "5", "6", "−"},
        {"1", "2", "3", "+"},
        {"1/L", "0", ".", "="}
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
            label.equals("×") || label.equals("−") || label.equals("+")) {
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
  private boolean isResultDisplayed = false;
  private void appendToScreen(String value) {
    TextView screen = findViewById(R.id.screen);
    String currentText = screen.getText().toString();

    if (value.equals("C")) {
      screen.setText("0"); // Reset to `0`
      updateTextSize(screen, 1); // Reset text size
      isResultDisplayed = false;
    } else if (value.equals("CE")) {
      if(isResultDisplayed)
      {
        screen.setText("0");
        updateTextSize(screen, 1);
      }
      else if (!currentText.isEmpty()) {
        int lastSpace = currentText.lastIndexOf(" ");
        if (lastSpace != -1) {
          String newText = currentText.substring(0, lastSpace);
          screen.setText(newText.isEmpty() ? "0" : newText);
          updateTextSize(screen, newText.length()); // Update text size
        } else {
          screen.setText("0"); // If only one number, reset to `0`
          updateTextSize(screen, 1); // Reset text size
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
      updateTextSize(screen, result.length()); // Update text size
    } else if (value.equals("1/L")) {
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
    }else if ((value.equals("+") || value.equals("-") || value.equals("×") || value.equals("÷")) && isResultDisplayed) {
      isResultDisplayed = false;
      screen.append(value);
    }
    else { // Number input
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
      updateTextSize(screen, screen.getText().length());
    }
  }
  private void updateTextSize(TextView screen, int length) {
    if (length <= 12) {
      screen.setTextSize(TypedValue.COMPLEX_UNIT_SP, 55); // Default size
    } else if (length > 12 && length <= 17) {
      screen.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40); // Smaller size
    }
  }
  private String formatExpressionForParser(String input) {
    return input
        .replace("×", " * ")
        .replace("÷", " / ")
        .replace("+", " + ")
        .replace("−", " - ");
  }
  private String calculateExpression(String input) {
    Expression expression = new Expression(input);
    double result = expression.calculate();

    if (Double.isNaN(result)) {
      isResultDisplayed = false;
      return "Error";
    }

    isResultDisplayed = true;
    String resultStr = (result == (int) result) ? String.valueOf((int) result) : String.valueOf(result);

    // If result exceeds 17 characters
    if (resultStr.length() > 17) {
      if (resultStr.contains(".")) {
        return resultStr.substring(0, 17); // Trim floating-point numbers
      } else {
        return String.format("%.6e", result); // Convert integers to scientific notation
      }
    }
    return resultStr;
  }


  private String applyReciprocal(String expression) {
    if (expression.isEmpty()) return expression;

    // Find the last number in the expression
    int lastOperatorIndex = -1;
    for (int i = expression.length() - 1; i >= 0; i--) {
      char c = expression.charAt(i);
      if (!Character.isDigit(c) && c != '.') {
        lastOperatorIndex = i;
        break;
      }
    }

    String lastNumber = expression.substring(lastOperatorIndex + 1); // Extract the last number
    if (lastNumber.isEmpty()) return expression; // If no number, return as is

    // Replace the last number with its reciprocal
    String newExpression = expression.substring(0, lastOperatorIndex + 1) + "1/(" + lastNumber + ")";
    return newExpression;
  }
}