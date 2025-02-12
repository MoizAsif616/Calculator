package com.example.calculator;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
  private void appendToScreen(String value) {
    EditText screen = findViewById(R.id.screen);
    screen.setText(screen.getText().toString() + value);
  }
  private void performOperation(String operator) {
    // Handle operator logic
  }
  private void calculateResult() {
    // Handle calculation logic
  }

//  private void switchTheme(){
//    ImageView themeSwitchIcon = findViewById(R.id.themeicon);
//    // Set initial icon based on current theme
//    int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//    if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
//      themeSwitchIcon.setImageResource(R.drawable.lightmodeicon); // Dark mode is active, show light icon
//    } else {
//      themeSwitchIcon.setImageResource(R.drawable.darkmodeicon); // Light mode is active, show dark icon
//    }
//
//    // Handle icon click to toggle theme
//    themeSwitchIcon.setOnClickListener(v -> {
//      if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Switch to light mode
//      } else {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // Switch to dark mode
//      }
//      recreate(); // Restart activity to apply new theme
//    });
//  }
}