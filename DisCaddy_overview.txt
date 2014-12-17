/********************************************************************************************/
TEAM MEMBERS
/********************************************************************************************/

Nick Dechant (ntd252)
Scott Stephens (sts768)
Nick Kubala (nk4682)

/********************************************************************************************/
BRIEF INSTRUCTIONS ON APP USE
/********************************************************************************************/

This app functions as a scorecard app for disc golf. Disc golf works just like normal ball golf, where the object is to get a disc into the basket of all 18 holes in as few strokes as possible. Each hole has a par associated with it (usually between 2 and 5 strokes).

The main functionality of the app is to create and save scorecards with player profiles associated with them. The app contains databases that save player profiles (with names, favorite courses and discs associated with them), courses (with names and pars associated with them), and scorecards (with game names, course names, player names, and scores associated with them).

To create a new scorecard, click the "New Scorecard" button. This will take the user to a screen where the course can be selected.

Once a course has been selected, the next step is to select the active players. The user will be taken to a screen where a list of players that have been created in the app is shown, and can be selected from to add to the new scorecard. Once all participating players have been added/created, the scorecard will be created.

The scorecard will consist of a screen for each hole on the course. On these screens, each player will be displayed along with his/her score. There are buttons for incrementing and decrementing the scores as needed.

Once the game is over, scorecards can be saved for later use.

/********************************************************************************************/
FEATURES COMPLETED
/********************************************************************************************/

Player profile creation and viewing (with name, favorite course/disc, scores, pictures from file or gallary, ect.).

Basic scorecard creation and viewing: active players, chosen course, scores for each player, ability to update scores as needed.

Course profile creation and viewing (with name, location, phone number, website, rating, pars,ect.).

Course lookup using google places API. Allows user to view courses close to them or search for a specific course.

Disc analyzer: uses online  FlightAnalyzer in web view to display various discs and their trajectory.

/********************************************************************************************/
FEATURES NOT ADDED THAT WERE SPECIFIED IN PROTOTYPE
/********************************************************************************************/

Report a found disk: Did not impliment an external database for this infomration.

Pars database: did not find or create an external database for course pars. User is required to input pars for each created course. 

/********************************************************************************************/
FEATURES ADDED THAT WEREN'T SPECIFIED IN PROTOTYPE
/********************************************************************************************/

None

/********************************************************************************************/
CODE CHUNKS/CLASSES OBTAINED FROM OTHER SOURCES
/********************************************************************************************/

The main code we took from other sources was code for custom adapters for displaying information. (http://www.vogella.com/tutorials/AndroidListView/article.html#adapterown_custom).

We used a bit of help for SQL queries as well. Used the android Note pad example to help create DiscaddyDbAdapter (http://developer.android.com/training/notepad/notepad-ex1.html)

Copied snippets from stack overflow to help with picture taking and getting intents (http://stackoverflow.com/questions/4455558/allow-user-to-select-camera-or-gallery-for-image). 
/********************************************************************************************/
ORIGINAL CODE/CLASSES
/********************************************************************************************/

All of the classes contained in this project are original, with the exception of snippets explained above. 
