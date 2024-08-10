package com.example.employeeapp.pages

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.employeeapp.reports.exportEventDetailsReportToCsv
import com.example.employeeapp.reports.exportEventDetailsReportToPdf
import com.example.employeeapp.reports.exportVolunteerParticipationReportToCsv
import com.example.employeeapp.reports.exportVolunteerParticipationReportToPdf
import java.io.File

@Composable
fun ReportsPage(context: Context) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Volunteer Participation Reports
        Button(onClick = {
            val pdfPath = exportVolunteerParticipationReportToPdf(context)
            if (pdfPath != null) {
                Toast.makeText(context, "PDF report generated successfully", Toast.LENGTH_SHORT).show()
                openFile(context, pdfPath, "application/pdf")
            } else {
                Toast.makeText(context, "Failed to generate PDF report", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Generate Volunteer Participation Report (PDF)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val csvPath = exportVolunteerParticipationReportToCsv(context)
            if (csvPath != null) {
                Toast.makeText(context, "CSV report generated successfully", Toast.LENGTH_SHORT).show()
                openFile(context, csvPath, "text/csv")
            } else {
                Toast.makeText(context, "Failed to generate CSV report", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Generate Volunteer Participation Report (CSV)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Event Details Reports
        Button(onClick = {
            val pdfPath = exportEventDetailsReportToPdf(context)
            if (pdfPath != null) {
                Toast.makeText(context, "PDF report generated successfully", Toast.LENGTH_SHORT).show()
                openFile(context, pdfPath, "application/pdf")
            } else {
                Toast.makeText(context, "Failed to generate PDF report", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Generate Event Details Report (PDF)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val csvPath = exportEventDetailsReportToCsv(context)
            if (csvPath != null) {
                Toast.makeText(context, "CSV report generated successfully", Toast.LENGTH_SHORT).show()
                openFile(context, csvPath, "text/csv")
            } else {
                Toast.makeText(context, "Failed to generate CSV report", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Generate Event Details Report (CSV)")
        }
    }
}

private fun openFile(context: Context, filePath: String, mimeType: String) {
    val file = File(filePath)
    val uri = Uri.fromFile(file)
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(uri, mimeType)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No suitable app found to open this file.", Toast.LENGTH_SHORT).show()
    }
}
