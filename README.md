# SC2002 Internship Management System

## Overview
- **Langauge**: java
- **Contributor**:
  - Allen: Boundary & Control Classes Coding in Java, [UML Sequence diagram](https://github.com/AIPEAC/SC2002-OOP/blob/main/Diagrams/Sequence%20Diagram.md).
  - Deryl: [UML Class diagram](https://github.com/AIPEAC/SC2002-OOP/blob/main/Diagrams/Class%20Diagram.md).
  - Hong Xun: [Testing Functionality](https://github.com/AIPEAC/SC2002-OOP/blob/main/Testcases/Testcase_Appendix_A/Test_Appendix_A.md), Entity Classes Coding in Java.
  - Jeremy: Wrote Report, Utility Class Coding in Java (UIHelper.java and ControlUtils.java)
  - Teja: Provided Filtering Algorithm.
- How to use:
  - **Download**
    - Download empty-database version for a fresh start of the program. [[Downlaod Link]](https://github.com/AIPEAC/SC2002-OOP/releases/download/Release-v1/Release-v1.zip)
    - Download presentation version to present directly. [[Downlaod Link]](https://github.com/AIPEAC/SC2002-OOP/releases/download/Presentation_v3/Presentation-v3.zip)
  - **Run the Program**
    - **Windows**: Distract the zip file you just download, right click on `run.ps1`, and click `Run with Powershell`
## What This Application Does

This is a desktop application built in Java that helps manage internship opportunities between students, companies, and career staff at a university. It handles the entire process from posting internships to students accepting offers.

## Highlights

- **Multi Window**: Allow multiple terminal windows run side by side, no delays.
- **Complete workflow**: Handles the entire internship process from start to finish
- **Role-based access**: Each user type sees only relevant features
- **Real-time validation**: Prevents common errors like double-applications or overbooking
- **Detailed reporting**: Staff can analyze trends and generate professional reports
- **User-friendly**: Simple GUI interface.

## Main Features

### For Students
- **Browse Internships**: View available internship opportunities matching your major
- **Apply for Internships**: Submit applications to internships that match your major
- **Track Applications**: See the status of your applications (pending, approved, rejected)
- **Accept/Reject Offers**: Once approved, you can accept or decline the internship offer
- **Withdraw Applications**: Cancel your application if you change your mind
- **View Applied Internships**: See all internships you've applied to in one place

### For Company Representatives
- **Post Internships**: Create new internship opportunities with details like:
  - Job title and description
  - Required majors (can select multiple)
  - Difficulty level (Basic, Intermediate, Advanced)
  - Number of available slots
  - Start and end dates
- **Manage Applications**: View and respond to student applications
- **Approve/Reject Students**: Accept or decline applications for your internships
- **Set Visibility**: Control whether your internship is visible to students

### For Career Staff
- **Oversee Everything**: View all internships and applications in the system
- **Handle Withdrawals**: Approve or reject student withdrawal requests
- **Generate Reports**: Create detailed reports about internship statistics including:
  - Number of internships by level and company
  - Popular majors for internships
  - Acceptance rates and slot utilization
  - Filtered reports by company, major, level, or date range

## How It Works

### User Authentication
- Users log in with their ID and password
- The system recognizes whether you're a student, company rep, or staff member
- Each user type sees different menus and options
- Company representatives can register new accounts themselves

### Application Process
1. **Company posts internship** with requirements and details
2. **Students browse and apply** to internships matching their major
3. **Company reviews applications** and approves/rejects candidates
4. **Students receive notifications** and can accept or reject approved offers
5. **System tracks everything** and prevents conflicts (like accepting multiple internships)

### Data Storage
- All information is stored in CSV files on your computer
- Includes separate files for users, internships, applications, and majors
- Uses escaping and unescaping methods to deal with special cases contain quotes and commas
- Reports are generated as Markdown files with tables and statistics

### Smart Features
- **Automatic slot management**: When an internship fills up, remaining applications are auto-rejected
- **Duplicate prevention**: Students can't apply to the same internship twice
- **Acceptance blocking**: Once a student accepts an internship, they can't apply to others
- **Filter preservation**: Reports show what filters were used for transparency

## User Interface

The application uses Java Swing with simple button-based menus:
- **Main menu** for each user type with clearly labeled buttons
- **Dialog boxes** for entering information (forms, confirmations)
- **Tables** for viewing lists of internships and applications
- **Multi-select lists** for choosing companies and majors in reports

## Technical Details

**Built with**: Java Swing for desktop interface  
**Data storage**: CSV files (no database required)  
**Security**: Password hashing with SHA-256 and salt  
**Reports**: Generated as Markdown files with statistics and charts  
**Architecture**: Separate layers for business logic, data storage, and user interface

This system replaces manual processes like email chains and spreadsheets with an organized, automated solution that reduces errors and saves time for everyone involved.
