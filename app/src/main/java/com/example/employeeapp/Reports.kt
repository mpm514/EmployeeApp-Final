package com.example.employeeapp.reports

import android.content.Context
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.File
import java.io.FileOutputStream
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.io.PrintWriter

import android.util.Log

fun generateVolunteerParticipationReport(context: Context): String {
    val usersFile = File(context.filesDir, "users.csv")
    val eventsFile = File(context.filesDir, "events.csv")
    val reportContent = StringBuilder()

    if (!usersFile.exists() || !eventsFile.exists()) return "No data available to generate report."

    try {
        BufferedReader(FileReader(usersFile)).use { userReader ->
            userReader.lineSequence().forEach { userLine ->
                val userData = userLine.split(",")
                if (userData.size >= 9) {
                    val volunteerName = userData[3] // Full Name
                    val skills = userData[8].split("|") // Skills
                    reportContent.append("""
                        Volunteer Name: $volunteerName
                        Skills: ${skills.joinToString(", ")}
                        Participation History:
                    """.trimIndent()).append("\n")

                    BufferedReader(FileReader(eventsFile)).use { eventReader ->
                        eventReader.lineSequence().forEach { eventLine ->
                            val eventData = eventLine.split(",")
                            if (eventData.size >= 9 && eventData[8].contains(volunteerName)) {
                                reportContent.append(" - Event: ${eventData[0]}, Date: ${eventData[5]}, Location: ${eventData[2]}\n")
                            }
                        }
                    }
                    reportContent.append("\n")
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    val reportString = reportContent.toString()
    Log.d("VolunteerParticipationReport", "Generated Report:\n$reportString")  // Log the report contents

    return reportString
}



fun exportVolunteerParticipationReportToPdf(context: Context): String? {
    val filePath = File(context.filesDir, "VolunteerParticipationReport.pdf").path

    try {
        val pdfWriter = PdfWriter(FileOutputStream(filePath))
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        val usersFile = File(context.filesDir, "users.csv")
        val eventsFile = File(context.filesDir, "events.csv")

        if (!usersFile.exists() || !eventsFile.exists()) return null

        BufferedReader(FileReader(usersFile)).use { userReader ->
            userReader.lineSequence().forEach { userLine ->
                val userData = userLine.split(",")
                if (userData.size >= 11) {
                    val volunteerName = userData[3]
                    val email = userData[0]
                    val location = userData[5]
                    val availability = userData[10].split("|").joinToString(", ")
                    val skills = userData[8].split("|").joinToString(", ")

                    document.add(Paragraph("Volunteer Name: $volunteerName"))
                    document.add(Paragraph("Email: $email"))
                    document.add(Paragraph("Location: $location"))
                    document.add(Paragraph("Availability: $availability"))
                    document.add(Paragraph("Skills: $skills"))
                    document.add(Paragraph("Participation History:"))

                    BufferedReader(FileReader(eventsFile)).use { eventReader ->
                        eventReader.lineSequence().forEach { eventLine ->
                            val eventData = eventLine.split(",")
                            if (eventData.size >= 9 && eventData[8].contains(volunteerName)) {
                                document.add(Paragraph(" - Event: ${eventData[0]}, Date: ${eventData[5]}, Location: ${eventData[2]}"))
                            }
                        }
                    }
                    document.add(Paragraph("\n"))
                }
            }
        }
        document.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    return filePath
}



fun exportVolunteerParticipationReportToCsv(context: Context): String? {
    val filePath = File(context.filesDir, "VolunteerParticipationReport.csv").path

    try {
        val writer = PrintWriter(FileOutputStream(filePath))
        writer.println("Volunteer Name,Email,Location,Availability,Skills,Event,Date,Location")

        val usersFile = File(context.filesDir, "users.csv")
        val eventsFile = File(context.filesDir, "events.csv")

        if (!usersFile.exists() || !eventsFile.exists()) return null

        BufferedReader(FileReader(usersFile)).use { userReader ->
            userReader.lineSequence().forEach { userLine ->
                val userData = userLine.split(",")
                if (userData.size >= 11) {
                    val volunteerName = userData[3]
                    val email = userData[0]
                    val location = userData[5]
                    val availability = userData[10].split("|").joinToString("; ")
                    val skills = userData[8].split("|").joinToString("; ")

                    BufferedReader(FileReader(eventsFile)).use { eventReader ->
                        eventReader.lineSequence().forEach { eventLine ->
                            val eventData = eventLine.split(",")
                            if (eventData.size >= 9 && eventData[8].contains(volunteerName)) {
                                writer.println("$volunteerName,$email,$location,$availability,$skills,${eventData[0]},${eventData[5]},${eventData[2]}")
                            }
                        }
                    }
                }
            }
        }
        writer.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    return filePath
}


fun generateEventDetailsReport(context: Context): String {
    val fileName = "events.csv"
    val file = File(context.filesDir, fileName)
    val reportContent = StringBuilder()

    if (!file.exists()) return "No data available to generate report."

    try {
        BufferedReader(FileReader(file)).use { reader ->
            reader.lineSequence().forEach { line ->
                val data = line.split(",")
                if (data.size >= 8) {
                    val skills = data[3].split("|")
                    val volunteers = if (data.size > 8) data[8].split("|") else listOf()
                    reportContent.append("""
                        Event Name: ${data[0]}
                        Description: ${data[1]}
                        Location: ${data[2]}
                        Required Skills: ${skills.joinToString(", ")}
                        Urgency: ${data[4]}
                        Date: ${data[5]}
                        Assigned Volunteers: ${volunteers.joinToString(", ")}
                    """.trimIndent()).append("\n\n")
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    Log.d("Reports", "Event Details Report Content:\n$reportContent")
    return reportContent.toString()
}

fun exportEventDetailsReportToPdf(context: Context): String? {
    val filePath = File(context.filesDir, "EventDetailsReport.pdf").path

    try {
        val pdfWriter = PdfWriter(FileOutputStream(filePath))
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        val eventsFile = File(context.filesDir, "events.csv")
        if (!eventsFile.exists()) return null

        BufferedReader(FileReader(eventsFile)).use { eventReader ->
            eventReader.lineSequence().forEach { eventLine ->
                val eventData = eventLine.split(",")
                if (eventData.size >= 9) {
                    val eventName = eventData[0]
                    val description = eventData[1]
                    val location = eventData[2]
                    val requiredSkills = eventData[3].split("|").joinToString(", ")
                    val urgency = eventData[4]
                    val date = eventData[5]
                    val assignedVolunteers = eventData[8].split("|").joinToString(", ")

                    document.add(Paragraph("Event Name: $eventName"))
                    document.add(Paragraph("Description: $description"))
                    document.add(Paragraph("Location: $location"))
                    document.add(Paragraph("Required Skills: $requiredSkills"))
                    document.add(Paragraph("Urgency: $urgency"))
                    document.add(Paragraph("Date: $date"))
                    document.add(Paragraph("Assigned Volunteers: $assignedVolunteers"))
                    document.add(Paragraph("\n"))
                }
            }
        }
        document.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    return filePath
}


fun exportEventDetailsReportToCsv(context: Context): String? {
    val filePath = File(context.filesDir, "EventDetailsReport.csv").path

    try {
        val writer = PrintWriter(FileOutputStream(filePath))
        writer.println("Event Name,Description,Location,Required Skills,Urgency,Date,Assigned Volunteers")

        val eventsFile = File(context.filesDir, "events.csv")
        if (!eventsFile.exists()) return null

        BufferedReader(FileReader(eventsFile)).use { eventReader ->
            eventReader.lineSequence().forEach { eventLine ->
                val eventData = eventLine.split(",")
                if (eventData.size >= 9) {
                    val eventName = eventData[0]
                    val description = eventData[1]
                    val location = eventData[2]
                    val requiredSkills = eventData[3].split("|").joinToString("; ")
                    val urgency = eventData[4]
                    val date = eventData[5]
                    val assignedVolunteers = eventData[8].split("|").joinToString("; ")

                    writer.println("$eventName,$description,$location,$requiredSkills,$urgency,$date,$assignedVolunteers")
                }
            }
        }
        writer.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    return filePath
}

