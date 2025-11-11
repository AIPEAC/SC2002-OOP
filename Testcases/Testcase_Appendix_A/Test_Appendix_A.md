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
| 9 | - | - | - | |
| 10 | Single Internship Placement Acceptance per Student | System allows accepting one internship placement and automatically withdraws all other applications once a placement is accepted | Student can accept more than one internship placement, or other applications remain active after accepting | |
| 11 | - | - | - | |
| 12 | - | - | - | |
| 13 | Company Representative Internship Opportunity Creation | System allows Company Representatives to create internship opportunities only when they meet system requirements | System allows creation of opportunities with invalid data or exceeds maximum allowed opportunities per representative | |
| 14 | Internship Opportunity Approval Status | Company Representatives can view pending, approved, or rejected status updates for their submitted opportunities | Status updates are not visible, incorrect, or not properly saved in the system | |
| 15 | Internship Detail Access for Company Representative | Company Representatives can always access full details of internship opportunities they created, regardless of visibility setting | Opportunity details become inaccessible when visibility is toggled off for their own opportunities | |
| 16 | Restriction on Editing Approved Opportunities | Edit functionality is restricted for Company Representatives once internship opportunities are approved by Career Center Staff | Company Representatives are able to make changes to opportunity details after approval | |
| 17 | - | - | - | |
| 18 | Student Application Management and Placement Confirmation | Company Representatives retrieve correct student applications, update slot availability accurately, and correctly confirm placement details | Incorrect application retrieval, slot counts not updating properly, or failure to reflect placement confirmation details accurately | |
| 19 | Internship Placement Confirmation Status Update | Placement confirmation status is updated to reflect the actual confirmation condition | System fails to update or incorrectly records the placement confirmation status | |
| 20 | Create, Edit, and Delete Internship Opportunity Listings | Company Representatives should be able to add new opportunities, modify existing opportunity details (before approval by Career Center Staff), and remove opportunities from the system | Inability to create, edit, or delete opportunities or errors during these operations | |
| 21 | Career Center Staff Internship Opportunity Approval | Career Center Staff can review and approve/reject internship opportunities submitted by Company Representatives | Career Center Staff cannot access submitted opportunities for review, approval/rejection actions fail to update opportunity status, or approved opportunities do not become visible to students as expected | |
| 22 | Toggle Internship Opportunity Visibility | Changes in visibility should be reflected accurately in the internship opportunity list visible to students | Visibility settings do not update or do not affect the opportunity listing as expected | |
| 23 | Career Center Staff Internship Opportunity Management Withdrawal | Withdrawal approvals and rejections are processed correctly, with system updates to reflect the decision and slot availability changes | Incorrect or failed processing of withdrawal requests or slot counts not updating properly | |
| 24 | Generate and Filter Internship Opportunities | Accurate report generation with options to filter by placement status, major, company, level, and other specified categories | Reports are inaccurate, incomplete, or filtering does not work as expected | |

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
| ---------- | ----------- | -------------- | 
| ![Company Representative Register](Testcase5_CompRepRegister.png) | ![New Company Representative Registered](Testcase5_RegisteredConfirmation.png) |![Unauthorised Login](Testcase5_UnauthorisedLogin.png) |
---

### Test Case 6: Internship Opportunity Visibility Based on User Profile and Toggle
- **Test Case:** Internship Opportunity Visibility Based on User Profile and Toggle
- **Expected Behavior:** Internship opportunities are visible to students based on their year of study, major, internship level eligibility, and the visibility setting
- **Actual Result:** 
- **Failure Indicator:** Students see internship opportunities not relevant to their profile (wrong major, wrong level for their year) or when visibility is off
- **Pass:** Yes
- **Screenshots:** 

| Career Staff | Student | Company Rep |
| ---------- | ----------- | -------------- | 
| ![Company Representative Register](Testcase5_CompRepRegister.png) | ![New Company Representative Registered](Testcase5_RegisteredConfirmation.png) |![Unauthorised Login](Testcase5_UnauthorisedLogin.png) |
---

### Test Case 7: Internship Application Eligibility
- **Test Case:** Internship Application Eligibility
- **Expected Behavior:** Students can only apply for internship opportunities relevant to their profile (correct major preference, appropriate level for their year of study) and when visibility is on
- **Actual Result:** 
- **Failure Indicator:** Students can apply for internship opportunities not relevant to their profile (wrong major preference, Basic-level students applying for Intermediate/Advanced opportunities) or when visibility is off
- **Pass:** 
- **Screenshots:** 

---

### Test Case 8: Viewing Application Status after Visibility Toggle Off
- **Test Case:** Viewing Application Status after Visibility Toggle Off
- **Expected Behavior:** Students continue to have access to their application details regardless of internship opportunities' visibility
- **Actual Result:** 
- **Failure Indicator:** Application details become inaccessible once visibility is off
- **Pass:** 
- **Screenshots:** 

---

### Test Case 9: -
- **Test Case:** -
- **Expected Behavior:** -
- **Actual Result:** 
- **Failure Indicator:** -
- **Pass:** 
- **Screenshots:** 

---

### Test Case 10: Single Internship Placement Acceptance per Student
- **Test Case:** Single Internship Placement Acceptance per Student
- **Expected Behavior:** System allows accepting one internship placement and automatically withdraws all other applications once a placement is accepted
- **Actual Result:** 
- **Failure Indicator:** Student can accept more than one internship placement, or other applications remain active after accepting
- **Pass:** 
- **Screenshots:** 

---

### Test Case 11: -
- **Test Case:** -
- **Expected Behavior:** -
- **Actual Result:** 
- **Failure Indicator:** -
- **Pass:** 
- **Screenshots:** 

---

### Test Case 12: -
- **Test Case:** -
- **Expected Behavior:** -
- **Actual Result:** 
- **Failure Indicator:** -
- **Pass:** 
- **Screenshots:** 

---

### Test Case 13: Company Representative Internship Opportunity Creation
- **Test Case:** Company Representative Internship Opportunity Creation
- **Expected Behavior:** System allows Company Representatives to create internship opportunities only when they meet system requirements
- **Actual Result:** 
- **Failure Indicator:** System allows creation of opportunities with invalid data or exceeds maximum allowed opportunities per representative
- **Pass:** 
- **Screenshots:** 

---

### Test Case 14: Internship Opportunity Approval Status
- **Test Case:** Internship Opportunity Approval Status
- **Expected Behavior:** Company Representatives can view pending, approved, or rejected status updates for their submitted opportunities
- **Actual Result:** 
- **Failure Indicator:** Status updates are not visible, incorrect, or not properly saved in the system
- **Pass:** 
- **Screenshots:** 

---

### Test Case 15: Internship Detail Access for Company Representative
- **Test Case:** Internship Detail Access for Company Representative
- **Expected Behavior:** Company Representatives can always access full details of internship opportunities they created, regardless of visibility setting
- **Actual Result:** 
- **Failure Indicator:** Opportunity details become inaccessible when visibility is toggled off for their own opportunities
- **Pass:** 
- **Screenshots:** 

---

### Test Case 16: Restriction on Editing Approved Opportunities
- **Test Case:** Restriction on Editing Approved Opportunities
- **Expected Behavior:** Edit functionality is restricted for Company Representatives once internship opportunities are approved by Career Center Staff
- **Actual Result:** 
- **Failure Indicator:** Company Representatives are able to make changes to opportunity details after approval
- **Pass:** 
- **Screenshots:** 

---

### Test Case 17: -
- **Test Case:** -
- **Expected Behavior:** -
- **Actual Result:** 
- **Failure Indicator:** -
- **Pass:** 
- **Screenshots:** 

---

### Test Case 18: Student Application Management and Placement Confirmation
- **Test Case:** Student Application Management and Placement Confirmation
- **Expected Behavior:** Company Representatives retrieve correct student applications, update slot availability accurately, and correctly confirm placement details
- **Actual Result:** 
- **Failure Indicator:** Incorrect application retrieval, slot counts not updating properly, or failure to reflect placement confirmation details accurately
- **Pass:** 
- **Screenshots:** 

---

### Test Case 19: Internship Placement Confirmation Status Update
- **Test Case:** Internship Placement Confirmation Status Update
- **Expected Behavior:** Placement confirmation status is updated to reflect the actual confirmation condition
- **Actual Result:** 
- **Failure Indicator:** System fails to update or incorrectly records the placement confirmation status
- **Pass:** 
- **Screenshots:** 

---

### Test Case 20: Create, Edit, and Delete Internship Opportunity Listings
- **Test Case:** Create, Edit, and Delete Internship Opportunity Listings
- **Expected Behavior:** Company Representatives should be able to add new opportunities, modify existing opportunity details (before approval by Career Center Staff), and remove opportunities from the system
- **Actual Result:** 
- **Failure Indicator:** Inability to create, edit, or delete opportunities or errors during these operations
- **Pass:** 
- **Screenshots:** 

---

### Test Case 21: Career Center Staff Internship Opportunity Approval
- **Test Case:** Career Center Staff Internship Opportunity Approval
- **Expected Behavior:** Career Center Staff can review and approve/reject internship opportunities submitted by Company Representatives
- **Actual Result:** 
- **Failure Indicator:** Career Center Staff cannot access submitted opportunities for review, approval/rejection actions fail to update opportunity status, or approved opportunities do not become visible to students as expected
- **Pass:** 
- **Screenshots:** 

---

### Test Case 22: Toggle Internship Opportunity Visibility
- **Test Case:** Toggle Internship Opportunity Visibility
- **Expected Behavior:** Changes in visibility should be reflected accurately in the internship opportunity list visible to students
- **Actual Result:** 
- **Failure Indicator:** Visibility settings do not update or do not affect the opportunity listing as expected
- **Pass:** 
- **Screenshots:** 

---

### Test Case 23: Career Center Staff Internship Opportunity Management Withdrawal
- **Test Case:** Career Center Staff Internship Opportunity Management Withdrawal
- **Expected Behavior:** Withdrawal approvals and rejections are processed correctly, with system updates to reflect the decision and slot availability changes
- **Actual Result:** 
- **Failure Indicator:** Incorrect or failed processing of withdrawal requests or slot counts not updating properly
- **Pass:** 
- **Screenshots:** 

---

### Test Case 24: Generate and Filter Internship Opportunities
- **Test Case:** Generate and Filter Internship Opportunities
- **Expected Behavior:** Accurate report generation with options to filter by placement status, major, company, level, and other specified categories
- **Actual Result:** 
- **Failure Indicator:** Reports are inaccurate, incomplete, or filtering does not work as expected
- **Pass:** 
- **Screenshots:** 

---

## Notes
- Add any additional observations or comments here
