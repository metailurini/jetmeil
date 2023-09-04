# JetMeil - metailurini's jetbrains plugin

> JetMeil is an extension of the Metailurini organization, aimed at providing several useful features to enhance the
> user experience.

## Features

### I. SVoice - Speak Selected Text

The first feature in JetMeil is SVoice, which allows the user to select a piece of text and have it spoken out loud.
This is particularly useful for users who prefer to listen to text instead of reading it.

#### Usage

To use the SVoice feature, simply select the text you want to hear, right-click and select "Speak Selected Text" from
the context menu. The selected text will then be spoken out loud using the default text-to-speech engine on your device.

## Installation

run the command

```shell
make install
```

## Future Features

JetMeil is constantly being developed, and new features will be added in the future. Stay tuned for updates and
improvements!

## Contributing

If you're interested in contributing to the JetMeil project, we'd love to have you! Please reach out to the Metailurini
team for more information.

## License

JetMeil is released under the MIT License.

## New Feature Roadmap

### Bookmarks storage

```
                      +-----+      +-----------+
                      | APP | ---> | MIGRATION |
                      +-----+      +-----------+
                                        |
                    +---------+         |
               +----| SQLITE3 | <-------+
               |    +---------+
               |         |
               |         |
               |         v
               |    +------------+------------+--[BOOKMARK]-+-----------+-------------+-----------+          | e.groupRenamed -> update group name for whole project
               |    | project_id | group_name | description | file_path | line_number | commit_id |  <-------| e.groupRemoved -> remove by group name
               |    +------------+------------+-------------+-----------+-------------+-----------+          | e.bookmarkAdded -> insert bookmark
               |          |   \____________________(PK)_________|____________/                               | e.bookmarkChanged -> update bookmark
               |          |
               |          v
               |    +------------+---[PROJECT]--+--------------+-------------+
               +--> | project_id | project name | project_path | github_link |
                    +------------+--------------+--------------+-------------+
```
