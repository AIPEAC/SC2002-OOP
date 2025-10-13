# Internship Placement Management System - Domain Model Draft Class Diagram

## I. Boundary Classes

| Class Name          |  Core Purpose                          |
|---------------------|---------------------------------------|
| StudentCLI          | Student operation entry (view opportunities, submit applications) |
| CompanyRepCLI       | Company Representative operation entry (create opportunities, review applications) |
| CareerStaffCLI      | Career Center Staff operation entry (approve accounts, approve opportunities, generate reports) |

## II. Control Classes

| Class Name                  | Core Purpose                          |
|-----------------------------|---------------------------------------|
| LoginControl                | Unified handling of user login and password change logic |
| ApplicationControl          | Handle application submission, review, and withdrawal logic |
| InternshipManagementControl | Handle internship opportunity creation, approval, and visibility toggle logic |
| ReportControl               | Handle report generation and filtering logic (for Career Center Staff only) |

## III. Entity Classes
| Class Name                  | Core Purpose                          |
|-----------------------------|---------------------------------------|
| User {abstract}             | Abstract common attributes of all users (ID, Name, Password) |
| Student                     | Store student info (Year of Study, Major) |
| CompanyRepresentative       | Store company rep info (Company Name, Department) |
| CareerStaff                 | Store Career Center Staff info (Staff Department) |
| InternshipOpportunity       | Store internship info (Title, Level, Closing Date, Slots) |
| Application                 | Store application records (Application Status, associated Student/Opportunity) |
| Report                      | Store generated internship report data (Filter Criteria, Content Summary) |

## IV. Core Interface
| Interface Name    | Core Business Behavior                |
|-------------------|---------------------------------------|
| Approval         | (approve()/reject()) |

## V. Class Relationships
(Somehow I don't think there is aggregation)  

### 1. Generalization
- `User {abstract}` — `Student`  
- `User {abstract}` — `CompanyRepresentative`  
- `User {abstract}` — `CareerStaff`  

### 2. Interface Realization
- `Approval` — `CareerStaff`  
- `Approval` — `CompanyRepresentative`

### 3. Association
- `Student` —[1:*]— `Application`  
- `InternshipOpportunity` —[1:*]— `Application`  
- `CompanyRepresentative` —[1:*]— `InternshipOpportunity`  
- `CareerStaff` —[1:*]— `CompanyRepresentative`  
- `CareerStaff` —[1:*]— `InternshipOpportunity`  
- `CareerStaff` —[1:*]— `Report` (this one maybe aggregation? so I am not sure)

### 4. Dependency
#### (1) Boundary → Control- `StudentCLI` —<、<use>>— `ApplicationControl`  
- `StudentCLI` —<\<use>>— `ApplicationControl`  
- `CompanyRepCLI` —<\<use>>— `InternshipManagementControl`  
- `CompanyRepCLI` —<\<use>>— `ApplicationControl`  
- `CareerStaffCLI` —<\<use>>— `InternshipManagementControl`  
- `CareerStaffCLI` —<\<use>>— `ReportControl`  

#### (2) Control → Entity/Interface
- `ApplicationControl` —<\<call>>— `Approval`  
- `LoginControl` —<\<read>>- `User`

## VI. (for later use)
`CompanyRepCLI` -> Create Opportunity (call `InternshipManagementControl`) -> `CareerStaffCLI` Approve Opportunity (call `InternshipManagementControl`) -> Opportunity marked "Approved" -> `StudentCLI` View Opportunity/Submit Application (call `ApplicationControl`) -> `CompanyRepCLI` Review Application (call `ApplicationControl`) -> Student accepts successful application -> Other applications automatically withdrawn
