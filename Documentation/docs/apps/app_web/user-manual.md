# User Manual

## Refereezy Web Application

This user manual provides detailed instructions and guidance for using the Refereezy Web Application for managing and monitoring sports matches.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Dashboard Navigation](#dashboard-navigation)
3. [Managing Teams](#managing-teams)
4. [Managing Players](#managing-players)
5. [Managing Referees](#managing-referees)
6. [Managing Matches](#managing-matches)
7. [Live Match Tracking](#live-match-tracking)
8. [Report Generation and Viewing](#report-generation-and-viewing)
9. [Administrator Functions](#administrator-functions)
10. [Account Settings](#account-settings)
11. [Troubleshooting](#troubleshooting)

## Getting Started

### Accessing the Application

The Refereezy Web Application can be accessed at [https://app.refereezy.com](https://app.refereezy.com).

### Creating an Account

1. Navigate to the application URL
2. Click on the "Register" button
3. Fill in the required information:
   - Organization Name
   - Administrator Name
   - Email Address
   - Password (must be at least 8 characters with a mix of letters, numbers, and symbols)
4. Select your subscription plan
5. Click "Create Account"
6. Verify your email address by clicking the link sent to your email

### Logging In

1. Navigate to the application URL
2. Enter your email address and password
3. Click "Log In"
4. For additional security, you may be prompted for two-factor authentication if enabled

![Login Screen](../images/web/login-screen.png)

### Navigating the Interface

The Refereezy interface consists of several key components:

- **Top Navigation Bar**: Access account settings, notifications, and language selection
- **Side Navigation Menu**: Navigate between different sections of the application
- **Main Content Area**: View and interact with the selected section's content
- **Action Buttons**: Perform actions relevant to the current section

## Dashboard Navigation

The dashboard is your starting point and provides an overview of your activity and upcoming matches.

### Overview Panel

The overview panel displays:

- Total number of teams, players, and referees
- Upcoming matches (next 7 days)
- Recently completed matches
- Active referees status

### Quick Actions

The quick actions section allows you to:

- Create a new match
- Add a new team
- Add a new player
- Generate reports

### Statistics and Analytics

The statistics section shows:

- Match activity over time
- Team performance metrics
- Referee assignment distribution
- System usage statistics

## Managing Teams

### Creating a New Team

1. Navigate to "Teams" in the side menu
2. Click the "Add Team" button
3. Fill in the team details:
   - Team Name
   - Primary Color (choose from color picker or enter hex code)
   - Secondary Color (choose from color picker or enter hex code)
   - Upload Team Logo (PNG or JPG, recommended size 256x256px)
4. Click "Create Team"

### Editing Team Information

1. Navigate to "Teams" in the side menu
2. Find the team you want to edit in the list
3. Click the "Edit" button (pencil icon) on the team card
4. Modify the desired information
5. Click "Save Changes"

### Deleting a Team

1. Navigate to "Teams" in the side menu
2. Find the team you want to delete in the list
3. Click the "Delete" button (trash icon) on the team card
4. Confirm deletion in the prompt that appears

> **Warning**: Deleting a team will remove all associated players and match records. This action cannot be undone.

## Managing Players

### Adding a Player

1. Navigate to "Players" in the side menu
2. Click the "Add Player" button
3. Fill in the player details:
   - Player Name
   - DNI (National ID Number)
   - Jersey Number
   - Team (select from dropdown)
   - Position (select from dropdown)
   - Is Goalkeeper (checkbox)
4. Optionally upload a player photo
5. Click "Add Player"

### Editing Player Information

1. Navigate to "Players" in the side menu
2. Find the player you want to edit
3. Click the "Edit" button (pencil icon) next to the player
4. Modify the desired information
5. Click "Save Changes"

### Transferring a Player

1. Navigate to "Players" in the side menu
2. Find the player you want to transfer
3. Click the "Transfer" button (arrow icon) next to the player
4. Select the new team from the dropdown
5. Click "Confirm Transfer"

### Removing a Player

1. Navigate to "Players" in the side menu
2. Find the player you want to remove
3. Click the "Delete" button (trash icon) next to the player
4. Confirm deletion in the prompt that appears

## Managing Referees

### Adding a Referee

1. Navigate to "Referees" in the side menu
2. Click the "Add Referee" button
3. Fill in the referee details:
   - Name
   - DNI (National ID Number)
   - Email
   - Phone Number
   - Password (or option to generate)
4. Click "Add Referee"

### Assigning Clock Codes

To pair a referee with a match clock:

1. Navigate to "Referees" in the side menu
2. Find the referee you want to pair with a clock
3. Click the "Assign Clock" button (clock icon)
4. Enter the clock code or generate a new one
5. Click "Assign Code"
6. A pairing code will be displayed - communicate this to the referee to enter on their watch app

### Managing Referee Permissions

1. Navigate to "Referees" in the side menu
2. Find the referee whose permissions you want to modify
3. Click the "Permissions" button (key icon)
4. Check or uncheck the appropriate permission boxes
5. Click "Save Permissions"

## Managing Matches

### Creating a Match

1. Navigate to "Matches" in the side menu
2. Click the "Create Match" button
3. Fill in the match details:
   - Local Team (select from dropdown)
   - Visitor Team (select from dropdown)
   - Date and Time
   - Venue (optional)
   - Referee (select from dropdown)
   - Match Group (optional, select from dropdown or create new)
4. Click "Create Match"

### Editing a Match

1. Navigate to "Matches" in the side menu
2. Find the match you want to edit
3. Click the "Edit" button (pencil icon)
4. Modify the desired information
5. Click "Save Changes"

### Cancelling a Match

1. Navigate to "Matches" in the side menu
2. Find the match you want to cancel
3. Click the "Cancel" button (X icon)
4. Enter a reason for cancellation
5. Click "Confirm Cancellation"

### Creating Match Groups

Match groups allow you to organize matches into tournaments, leagues, or other groupings:

1. Navigate to "Match Groups" in the side menu
2. Click "Create Match Group"
3. Enter the group details:
   - Name
   - Description (optional)
   - Visibility (Public or Private)
   - Access Code (for private groups)
4. Click "Create Group"

## Live Match Tracking

### Accessing Live Match View

1. Navigate to "Matches" in the side menu
2. Find the match you want to view
3. If the match is currently active, a "Live" indicator will be displayed
4. Click on the match card to enter the live view

![Live Match View](../images/web/live-match-view.png)

### Understanding the Live Match Interface

The live match interface includes:

- **Scoreboard**: Displays current score, team names and logos
- **Match Clock**: Shows current match time and period
- **Incident Feed**: Real-time feed of match events (goals, cards, fouls)
- **Team Statistics**: Live statistics for both teams
- **Player Performance**: Individual player statistics and events

### Sharing Live Match Links

To share a live match with others:

1. In the live match view, click the "Share" button
2. Choose whether to share:
   - Public link (anyone can view)
   - Private link (requires access code)
3. Copy the generated link to share via email or messaging

## Report Generation and Viewing

### Viewing Match Reports

1. Navigate to "Reports" in the side menu
2. Browse the list of available reports
3. Click on a report to view its details
4. Use the filters to sort reports by date, team, or status

### Generating Reports Manually

1. Navigate to "Reports" in the side menu
2. Click "Generate Report"
3. Select the match from the dropdown
4. Click "Generate"
5. The system will compile all match data into a structured report

### Downloading and Printing Reports

1. Open the desired report
2. Click the "Export" button
3. Select your preferred format:
   - PDF (official format)
   - CSV (data only)
   - JSON (for system integration)
4. If exporting as PDF, choose whether to include:
   - Signature page
   - Detailed incidents
   - Player statistics
5. Click "Export" to download the file

### Sharing Reports

1. Open the desired report
2. Click the "Share" button
3. Enter email addresses of recipients
4. Add an optional message
5. Click "Send"

## Administrator Functions

### User Management

1. Navigate to "Admin" > "Users" in the side menu
2. View all users in the system
3. Click on a user to view details
4. Use the "Edit" button to modify user information
5. Use the "Deactivate" button to temporarily disable an account

### Subscription Management

1. Navigate to "Admin" > "Subscription" in the side menu
2. View current subscription details
3. Click "Change Plan" to upgrade or downgrade
4. Update billing information as needed
5. View invoice history

### System Configuration

1. Navigate to "Admin" > "Settings" in the side menu
2. Configure system-wide settings:
   - Default match duration
   - Default number of periods
   - Time format (12h/24h)
   - Notification preferences
   - Report templates

## Account Settings

### Updating Profile Information

1. Click your profile icon in the top-right corner
2. Select "Profile Settings"
3. Update your information:
   - Name
   - Email
   - Phone Number
   - Profile Picture
4. Click "Save Changes"

### Changing Password

1. Click your profile icon in the top-right corner
2. Select "Security Settings"
3. Click "Change Password"
4. Enter your current password
5. Enter and confirm your new password
6. Click "Update Password"

### Setting Up Two-Factor Authentication

1. Click your profile icon in the top-right corner
2. Select "Security Settings"
3. Click "Enable Two-Factor Authentication"
4. Choose your preferred method:
   - Authenticator App
   - SMS
   - Email
5. Follow the on-screen instructions to complete setup

### Language Preferences

1. Click the language selector in the top navigation bar
2. Choose your preferred language:
   - English
   - Català (Catalan)
   - Español (Spanish)
3. The interface will immediately update to reflect your selection

## Troubleshooting

### Common Issues and Solutions

#### Unable to Log In

- Verify your email address and password
- Check if Caps Lock is enabled
- Use the "Forgot Password" link to reset your password
- Ensure your account hasn't been deactivated

#### Match Data Not Updating in Real-time

- Check your internet connection
- Refresh the page
- Clear your browser cache
- Try using a different browser

#### Report Generation Fails

- Ensure the match has been completed
- Verify all required information is present
- Check that the referee has finalized the match data
- Try generating the report again after a few minutes

### Getting Support

If you encounter issues not covered in this manual:

1. Click the "Help" icon in the bottom-right corner
2. Search the knowledge base for your issue
3. Click "Contact Support" if you can't find a solution
4. Fill in the support request form with detailed information
5. A support representative will respond via email

### System Requirements

The Refereezy Web Application works best with:

- **Browsers**: Latest versions of Chrome, Firefox, Safari, or Edge
- **Internet Connection**: Broadband connection (at least 2 Mbps)
- **Display**: Minimum resolution of 1280x720
- **Devices**: Desktop, laptop, or tablet (some features limited on mobile)

## Appendix

### Keyboard Shortcuts

For power users, the following keyboard shortcuts are available:

- `Alt + D`: Go to Dashboard
- `Alt + M`: Go to Matches
- `Alt + T`: Go to Teams
- `Alt + P`: Go to Players
- `Alt + R`: Go to Reports
- `Alt + S`: Search
- `Alt + H`: Help
- `Alt + A`: Admin Panel (if applicable)

### Glossary

- **DNI**: National identity document number
- **Match Group**: Collection of related matches (tournament, league, etc.)
- **Client**: Organization or entity using the Refereezy platform
- **Clock Code**: Unique code used to pair referee devices with matches

---

*For additional help or to provide feedback on this manual, please contact documentation@refereezy.com*
