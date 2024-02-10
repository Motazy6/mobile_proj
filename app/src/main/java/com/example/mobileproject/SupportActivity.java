package com.example.mobileproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        Button buttonFAQ = findViewById(R.id.buttonFAQ);
        Button buttonContactUs = findViewById(R.id.buttonContactUs);
        Button buttonReportIssue = findViewById(R.id.buttonReportIssue);

        buttonFAQ.setOnClickListener(v -> openFAQSection());
        buttonContactUs.setOnClickListener(v -> showContactDialog());
        buttonReportIssue.setOnClickListener(v -> openIssueReportForm());
    }

    private void openFAQSection() {
        Toast.makeText(this, "FAQ section coming soon!", Toast.LENGTH_LONG).show();
    }

    private void showContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_contact, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button buttonSendEmail = dialogView.findViewById(R.id.buttonSendEmail);
        Button buttonCall = dialogView.findViewById(R.id.buttonCall);

        buttonSendEmail.setOnClickListener(v -> {
            sendEmail("admin@student-housing.ps");
            dialog.dismiss();
        });

        buttonCall.setOnClickListener(v -> {
            callPhoneNumber("059-xxxxxxx");
            dialog.dismiss();
        });

        dialog.show();
    }

    private void sendEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void callPhoneNumber(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    private void openIssueReportForm() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"admin@student-housing.ps"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Issue Report");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SupportActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
