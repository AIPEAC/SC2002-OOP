# Test Cases - Appendix A

## Test Case Summary

| Index | Test Case | Expected Behavior | Failure Indicator | Pass (Y/N) |
|-------|-----------|-------------------|-------------------|------------|
| 1 | Valid User Login | User should be able to access their dashboard based on their roles | User cannot log in or receive incorrect error messages | Y |
| 2 | Invalid ID | User receives a notification about incorrect ID | User is allowed to log in with an invalid ID or fail to provide meaningful error message | |
| 3 | Incorrect Password | System should deny access and alert the user to incorrect password | User logs in successfully with a wrong password or fail to provide meaningful error message | |
| 4 | Password Change Functionality | System updates password, and allows login with new credentials | System does not update the password or denies access with the new password | |
| 5 | Company Representative Account Creation | A new Company Representative should only be able to log in to their account after it has been approved by a Career Center Staff | Company Representative staff can log in without any authorization | |
| 6 | Internship Opportunity Visibility Based on User Profile and Toggle | Internship opportunities are visible to students based on their year of study, major, internship level eligibility, and the visibility setting | Students see internship opportunities not relevant to their profile (wrong major, wrong level for their year) or when visibility is off | |
| 7 | Internship Application Eligibility | Students can only apply for internship opportunities relevant to their profile (correct major preference, appropriate level for their year of study) and when visibility is on | Students can apply for internship opportunities not relevant to their profile (wrong major preference, Basic-level students applying for Intermediate/Advanced opportunities) or when visibility is off | |
| 8 | Viewing Application Status after Visibility Toggle Off | Students continue to have access to their application details regardless of internship opportunities' visibility | Application details become inaccessible once visibility is off | |
| 9 | Single Internship Placement Acceptance per Student | System allows accepting one internship placement and automatically withdraws all other applications once a placement is accepted | Student can accept more than one internship placement, or other applications remain active after accepting | |
| 10 | Company Representative Internship Opportunity Creation | System allows Company Representatives to create up to 5 internship opportunities | System allows creation of opportunities even when they exceed the maximum allowed of 5 opportunities per representative | |
| 11 | Internship Opportunity Approval Status | Company Representatives can view pending, approved, or rejected status updates for their submitted opportunities | Status updates are not visible, incorrect, or not properly saved in the system | |
| 12 | Internship Detail Access for Company Representative | Company Representatives can always access full details of internship opportunities they created, regardless of visibility setting | Opportunity details become inaccessible when visibility is toggled off for their own opportunities | |
| 13 | Restriction on Editing Approved Opportunities | Edit functionality is restricted for Company Representatives once internship opportunities are approved by Career Center Staff | Company Representatives are able to make changes to opportunity details after approval | |
| 14 | Student Application Management and Placement Confirmation | Company Representatives retrieve correct student applications, and update slot availability accurately | Incorrect application retrieval, or slot counts not updating properly | |
| 15 | Internship Placement Confirmation Status Update | Placement confirmation status is updated to reflect the actual confirmation condition | System fails to update or incorrectly records the placement confirmation status | |
| 16 | Create, Edit, and Delete Internship Opportunity Listings | Company Representatives should be able to add new opportunities, modify existing opportunity details (before approval by Career Center Staff), and remove opportunities from the system | Inability to create, edit, or delete opportunities or errors during these operations | |
| 17 | Career Center Staff Internship Opportunity Approval | Career Center Staff can review and approve/reject internship opportunities submitted by Company Representatives | Career Center Staff cannot access submitted opportunities for review, approval/rejection actions fail to update opportunity status, or approved opportunities do not become visible to students as expected | |
| 18 | Toggle Internship Opportunity Visibility | Changes in visibility should be reflected accurately in the internship opportunity list visible to students | Visibility settings do not update or do not affect the opportunity listing as expected | |
| 19 | Career Center Staff Internship Opportunity Management Withdrawal | Withdrawal approvals and rejections are processed correctly, with system updates to reflect the decision and slot availability changes | Incorrect or failed processing of withdrawal requests or slot counts not updating properly | |
| 20 | Generate and Filter Internship Opportunities | Accurate report generation with options to filter by placement status, major, company, level, and other specified categories | Reports are inaccurate, incomplete, or filtering does not work as expected | |

---

## Detailed Test Results

### Test Case 1: Valid User Login
- **Test Case:** Valid User Login
- **Expected Behavior:** User should be able to access their dashboard based on their roles
- **Actual Result:** Successfully logged in to Staff/Student/CompanyRep's respective Interface upon enter the correct username and password.
- **Failure Indicator:** User cannot log in or receive incorrect error messages
- **Pass:** Yes
- **Screenshots:** 

| Career Staff | Student | Company Rep |
| ---------- | ----------- | -------------- | 
| ![login staff](Testcase1_Staff.png) | ![login Student](Testcase1_Student.png) |![login CompRep](Testcase1_CompanyRep.png)|
---


### Test Case 2: Invalid ID
- **Test Case:** Invalid ID
- **Expected Behavior:** User receives a notification about incorrect ID
- **Actual Result:** 
- **Failure Indicator:** User is allowed to log in with an invalid ID or fail to provide meaningful error message
- **Pass:** Yes
- **Screenshots:** 
 
| Invid ID | Notification |
| -------------------------------------- | -------------------------------------- | 
| ![Invalid ID](Testcase2_InvalidID.png) | ![Notification](Testcase2_NotificationID.png) |
---

### Test Case 3: Incorrect Password
- **Test Case:** Incorrect Password
- **Expected Behavior:** System should deny access and alert the user to incorrect password
- **Actual Result:** 
- **Failure Indicator:** User logs in successfully with a wrong password or fail to provide meaningful error message
- **Pass:** Yes
- **Screenshots:** 

| Career Staff | Student | Company Rep | Invalid PW |
| ---------- | ----------- | -------------- | -------------- | 
| ![PW staff](Testcase3_StaffPW.png) | ![PW Student](Testcase3_StudentPW.png) |![PW CompRep](Testcase3_CompanyRepPW.png) | ![InvalidPW](Testcase3_NotificationPW.png) |
---

### Test Case 4: Password Change Functionality
- **Test Case:** Password Change Functionality
- **Expected Behavior:** System updates password, and allows login with new credentials
- **Actual Result:** 
- **Failure Indicator:** System does not update the password or denies access with the new password
- **Pass:** Yes
- **Screenshots:** 

| Initial Password Login | Initial Login Success | Password Change | Password Change Confirmation | Final Password Login | Final Login Success |
| ---------- | ----------- | -------------- | -------------- | -------------- | -------------- | 
|![Initial Password Login](Testcase4_InitialLogin.png) |![Initial Login Success](Testcase4_InitialLoginSuccess.png) | ![Password Change](Testcase4_ChangePW.png) | ![Password Change Confirmation](Testcase4_ConfirmationChangedPW.png) | ![Final Password Login](Testcase4_FinalLogin.png) | ![Final Login Success](Testcase4_FinalLoginSuccess.png) |
---

### Test Case 5: Company Representative Account Creation
- **Test Case:** Company Representative Account Creation
- **Expected Behavior:** A new Company Representative should only be able to log in to their account after it has been approved by a Career Center Staff
- **Actual Result:** 
- **Failure Indicator:** Company Representative staff can log in without any authorization
- **Pass:** Yes
- **Screenshots:** 

| Register | Notification | Unauthorised Login |
| -------- | ------------ | ------------------ | 
| ![Company Representative Register](Testcase5_CompRepRegister.png) | ![New Company Representative Registered](Testcase5_RegisteredConfirmation.png) |![Unauthorised Login](Testcase5_UnauthorisedLogin.png) |
---

### Test Case 6: Internship Opportunity Visibility Based on User Profile and Toggle
- **Test Case:** Internship Opportunity Visibility Based on User Profile and Toggle
- **Expected Behavior:** Internship opportunities are visible to students based on their year of study, major, internship level eligibility, and the visibility setting
- **Actual Result:** 
- **Failure Indicator:** Students see internship opportunities not relevant to their profile (wrong major, wrong level for their year) or when visibility is off
- **Pass:** Yes
- **Screenshots:** 

| Internship Opportunities | Y1 Student Info View | Y3 Student DSAI View |
| ------------------------ | -------------------- | -------------------- | 
| ![All Internship Opportunities](Testcase6_AllIntOp.png) | ![Y1 Student Info View](Testcase6_Y1StudentInfo.png) |![Y3 Student DSAI View](Testcase6_Y3StudentDSAI.png) |
---

### Test Case 7: Internship Application Eligibility
- **Test Case:** Internship Application Eligibility
- **Expected Behavior:** Students can only apply for internship opportunities relevant to their profile (correct major preference, appropriate level for their year of study) and when visibility is on
- **Actual Result:** 
- **Failure Indicator:** Students can apply for internship opportunities not relevant to their profile (wrong major preference, Basic-level students applying for Intermediate/Advanced opportunities) or when visibility is off
- **Pass:** Yes
- **Screenshots:** 

| Internship Opportunities | Student Apply Choices |
| ------------------------ | --------------------- | 
| ![All Internship Opportunities](Testcase7_AllIntOp.png) | ![Y1 Student Info View (apply button grayed out)](Testcase7_Y1StudentInfo.png) |
---

### Test Case 8: Viewing Application Status after Visibility Toggle Off
- **Test Case:** Viewing Application Status after Visibility Toggle Off
- **Expected Behavior:** Students continue to have access to their application details regardless of internship opportunities' visibility
- **Actual Result:** 
- **Failure Indicator:** Application details become inaccessible once visibility is off
- **Pass:** Yes
- **Screenshots:** 

| Visibility Off | Student Try To View App Details |
| ---------------- | ------------------ | 
| ![Visibility Off](Testcase8_VisOff.png) | ![Student View App Details](Testcase8_AppDetails.png) |
---

### Test Case 9: Single Internship Placement Acceptance per Student
- **Test Case:** Single Internship Placement Acceptance per Student
- **Expected Behavior:** System allows accepting one internship placement and automatically withdraws all other applications once a placement is accepted
- **Actual Result:** 
- **Failure Indicator:** Student can accept more than one internship placement, or other applications remain active after accepting
- **Pass:** Yes
- **Screenshots:** 

| Multiple Internship Opportunity | Student Accept First | Updated Status (Note Second Internship Withdrawn Status Is Now Approved) | Student Try To Accept Second |
| ------------------------------- | -------------------- | ---------------------------- | -----------------------|
| ![Student View Multiple Internship Op](Testcase9_MultIntOp.png) | ![First Internship Op Accept](Testcase9_FirstAccept.png) | ![Updated Status](Testcase9_UpdatedStatus.png) | ![Second Internship Op Accept](Testcase9_SecondAccept.png) |
---

### Test Case 10: Company Representative Internship Opportunity Creation
- **Test Case:** Company Representative Internship Opportunity Creation
- **Expected Behavior:** System only allows Company Representatives to create up to 5 internship opportunities
- **Actual Result:** 
- **Failure Indicator:** System allows creation of opportunities even when it exceeds the maximum allowed of 5 opportunities per representative
- **Pass:** Yes
- **Screenshots:** 

| Trying to create 6th Internship Opportunity | All Internship Opportunities By Comp Rep |
| ------------------------------------------- | ---------------------------------------- |
| ![6th Internship Op Attempt](Testcase10_ExceedFive.png) | ![All Int Op](Testcase10_AllIntOp.png) |
---

### Test Case 11: Internship Opportunity Approval Status
- **Test Case:** Internship Opportunity Approval Status
- **Expected Behavior:** Company Representatives can view pending, approved, or rejected status updates for their submitted opportunities
- **Actual Result:** 
- **Failure Indicator:** Status updates are not visible, incorrect, or not properly saved in the system
- **Pass:** Yes
- **Screenshots:** 

| Company Representative View Internship Opportunity Status |
| --------------------------------------------------------- |
| ![Company Representative View Internship Op Status](Testcase11_IntOpStatus.png) |
---

### Test Case 12: Internship Detail Access for Company Representative
- **Test Case:** Internship Detail Access for Company Representative
- **Expected Behavior:** Company Representatives can always access full details of internship opportunities they created, regardless of visibility setting
- **Actual Result:** 
- **Failure Indicator:** Opportunity details become inaccessible when visibility is toggled off for their own opportunities
- **Pass:** Yes
- **Screenshots:** 

| Details Always Visible (Details and Visibility Seperate Option) |
| --------------------------------------------------------------- | 
| ![Details Visible](Testcase12_DetailsVisible.png) |
---

### Test Case 13: Restriction on Editing Approved Opportunities
- **Test Case:** Restriction on Editing Approved Opportunities
- **Expected Behavior:** Edit functionality is restricted for Company Representatives once internship opportunities are approved by Career Center Staff
- **Actual Result:** 
- **Failure Indicator:** Company Representatives are able to make changes to opportunity details after approval
- **Pass:** 
- **Screenshots:** 

---

### Test Case 14: Student Application Management
- **Test Case:** Student Application Management
- **Expected Behavior:** Company Representatives retrieve correct student applications, and update slot availability accurately
- **Actual Result:** 
- **Failure Indicator:** Incorrect application retrieval, or slot counts not updating properly
- **Pass:** Yes
- **Screenshots:** 

| Student 1 Applies | Student 2 Applies | Company Representative Retrieve Application | Not Full | Approving Both Student | Student 1 Accept | Student 2 Cannot Accept (Auto Reject)| Full Slot (Because only 1 slot allowed)|
| --------------------- | ------------ | ------------- | --------- | -------- | --------- | --------- | -------- |
| ![Student 1 Applies](Testcase14_StudentApplication.png) | ![Student 2 Applies](Testcase14_Student2Application.png) | ![Application Retrieval](Testcase14_AppRetrieval.png) | ![Not Full](Testcase14_NotFull.png) | ![Approving Both Student](Testcase14_Approve1.png) | ![Student 1 Accept](Testcase14_Student1Accept.png) | ![Student 2 Cannot Accept](Testcase14_CannotAccept.png) | ![Full Slot](Testcase14_FullSlot.png)
---

### Test Case 15: Internship Placement Confirmation Status Update
- **Test Case:** Internship Placement Confirmation Status Update
- **Expected Behavior:** Placement confirmation status is updated to reflect the actual confirmation condition
- **Actual Result:** 
- **Failure Indicator:** System fails to update or incorrectly records the placement confirmation status
- **Pass:** Yes
- **Screenshots:** 
| Student Accept Internship Opportunity | Placement Confirmation Status |
| ------------------------------------- | ----------------------------- |
| ![Student Accept](Testcase15_StudentAccept.png) | ![Placement Confirmation Status](Testcase15_AppStatusConfirmed.png) |
---

### Test Case 16: Create, Edit, and Delete Internship Opportunity Listings
- **Test Case:** Create, Edit, and Delete Internship Opportunity Listings
- **Expected Behavior:** Company Representatives should be able to add new opportunities, modify existing opportunity details (before approval by Career Center Staff), and remove opportunities from the system
- **Actual Result:** 
- **Failure Indicator:** Inability to create, edit, or delete opportunities or errors during these operations
- **Pass:** 
- **Screenshots:** 

---

### Test Case 17: Career Center Staff Internship Opportunity Approval
- **Test Case:** Career Center Staff Internship Opportunity Approval
- **Expected Behavior:** Career Center Staff can review and approve/reject internship opportunities submitted by Company Representatives
- **Actual Result:** 
- **Failure Indicator:** Career Center Staff cannot access submitted opportunities for review, approval/rejection actions fail to update opportunity status, or approved opportunities do not become visible to students as expected
- **Pass:** Yes
- **Screenshots:** 

| Comp Rep making Internship Opportunity | Staff Viewing Pending Internship Opportunity | Internship Opportunity approval | internship Opportunity Rejection | Internship Oportunity Status | Student Viewing Internship Opportunity |
| -------------- | ------------------ | -------------------- | ---------------------- | -------------- |
| ![Internship Op Creation](Testcase17_IntOpCreation.png) | ![Staff Viewing Pending Internship Op](Testcase17_StaffViewPendIntOp.png) | ![Internship Op Approved](Testcase17_Approved.png) | ![Internship Op Rejected](Testcase17_Rejected.png) | ![Internship Op Status](Testcase17_IntOpStatus.png) | ![Student View](Testcase17_StudentView.png) |

---

### Test Case 18: Toggle Internship Opportunity Visibility
- **Test Case:** Toggle Internship Opportunity Visibility
- **Expected Behavior:** Changes in visibility should be reflected accurately in the internship opportunity list visible to students
- **Actual Result:** 
- **Failure Indicator:** Visibility settings do not update or do not affect the opportunity listing as expected
- **Pass:** Yes
- **Screenshots:** 

| All NewComp Internship Opportunity | Student Viewing Internship Opportunity |
| ---------------------------------- | -------------------------------------- |
| ![NewComp Internship Op](Testcase18_AllIntOp.png) | ![Student View](Testcase18_StudentView.png) |
---

### Test Case 19: Career Center Staff Internship Opportunity Management Withdrawal
- **Test Case:** Career Center Staff Internship Opportunity Management Withdrawal
- **Expected Behavior:** Withdrawal approvals and rejections are processed correctly, with system updates to reflect the decision and slot availability changes
- **Actual Result:** 
- **Failure Indicator:** Incorrect or failed processing of withdrawal requests or slot counts not updating properly
- **Pass:** Yes
- **Screenshots:** 

| Student 1 Withdraw From Full Internship Opportunity | Staff Rejects | Withdraw Reject Status Update | Slot still Full | Student 2 Withdraw From Full Internship Opportunity | Staff Approves | Withdraw Approve Status Update | Slot No Longer Full |
| ---------------------------------- | -------------------------------------- |
| ![Student 1 Withdraw From Full](Testcase19_StudWithdrawFull.png) | ![Staff Rejects](Testcase19_Reject.png) | ![Withdraw Reject Status](Testcase19_WithdrawRejected.png) | ![Slot Still Full](Testcase19_Full.png) | ![Student 2 WIthdraw From Full](Testcase19_Stud2WithdrawFull.png) | ![Staff Approves](Testcase19_Approve.png) | ![Withdraw Approve Status](Testcase19_WithdrawApproved.png) | ![Slot Not Full](Testcase19_NotFull.png) |
---

### Test Case 20: Generate and Filter Internship Opportunities
- **Test Case:** Generate and Filter Internship Opportunities
- **Expected Behavior:** Accurate report generation with options to filter by placement status, major, company, level, and other specified categories
- **Actual Result:** 
- **Failure Indicator:** Reports are inaccurate, incomplete, or filtering does not work as expected
- **Pass:** Yes
- **Screenshots:** 

| NewComp Internship List (3 Full)| NewComp2 Internship List (0 Full)| Generated Overview Report (No Filter) | NewComp filtered Report | Basic Internship Level Report | Info and CS Major Filtered Report | Added 2026 Internship Opportunity | 2026 Internship Report | Filter Panel (For Upcoming image) |2025, Only CS or Civil Engineering or Chinese Majors, Advanced Level, From NewComp2 |
| ------------------------------------- |
| ![NewComp Internship List](Testcase20_NewCompInternList.png) | ![NewComp2 Internship List](Testcase20_NewComp2InternList.png) | ![Generated Overview Report](Testcase20_OverviewReport.png) | ![NewComp Report](Testcase20_NewCompReport.png) | ![Basic Internship Level Report](Testcase20_BasicInternLevelReport.png) | ![Info and CS Major Filtered Report](Testcase20_InfoAndCSMajorReport.png) | ![Added 2026 Internship Opportunity](Testcase20_Added2026InternOp.png) | ![2026 Internship Report](Testcase20_2026Report.png) | ![Filter Panel](Testcase20_FilterPanel.png) | ![Assorted Filters](Testcase20_Filtered.png) |
---

## Notes
- Add any additional observations or comments here
