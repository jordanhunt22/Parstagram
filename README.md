# Project 4 - *Parstagram*

**Parstagram** is a photo sharing app using Parse as its backend.

Time spent: **30** hours spent in total

## User Stories

The following **required** functionality is completed:

- [X] User sees app icon in home screen.
- [X] User can sign up to create a new account using Parse authentication
- [X] User can log in and log out of his or her account
- [X] The current signed in user is persisted across app restarts
- [X] User can take a photo, add a caption, and post it to "Instagram"
- [X] User can view the last 20 posts submitted to "Instagram"
- [X] User can pull to refresh the last 20 posts submitted to "Instagram"
- [X] User can tap a post to view post details, including timestamp and caption.

The following **stretch** features are implemented:

- [X] Style the login page to look like the real Instagram login page.
- [X] Style the feed to look like the real Instagram feed.
- [X] User should switch between different tabs - viewing all posts (feed view), capture (camera and photo gallery view) and profile tabs (posts made) using fragments and a Bottom Navigation View.
- [X] User can load more posts once he or she reaches the bottom of the feed using endless scrolling.
- [X] Show the username and creation time for each post
- [X] After the user submits a new post, show an indeterminate progress bar while the post is being uploaded to Parse
- User Profiles:
  - [X] Allow the logged in user to add a profile photo
  - [X] Display the profile photo with each post
  - [X] Tapping on a post's username or profile photo goes to that user's profile page
  - [X] User Profile shows posts in a grid view
- [X] User can comment on a post and see all comments for each post in the post details screen.
- [ ] User can like a post and see number of likes for each post in the post details screen.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='parstagram.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' /> <img src='Parstagram2.gif' title='Video Walkthrough' width='' alt='Video Walkthrough 2' />

GIF created with [Kap].

## Credits

List an 3rd party libraries, icons, graphics, or other assets you used in your app.

- [Android Async Http Client](http://loopj.com/android-async-http/) - networking library


## Notes

Describe any challenges encountered while building the app.

It was hard for me to implement the likes feature because I was having trouble storing likes. In the future, I would like to figure out how to better use JSON Arrays to store data.

## License

    Copyright [2020] [Jordan]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
