# Pass the Pigs - Mobile App Specification

## Overview
A Kotlin Multiplatform Compose (KMP) mobile application for keeping score in the classic dice game "Pass the Pigs". The app provides an intuitive scoring interface with easily identifiable buttons and smart user management.

## Game Background
Pass the Pigs is a dice game where players take turns rolling two plastic pig-shaped dice. Players score points based on how the pigs land and must decide whether to continue rolling (risking their turn score) or bank their points and pass to the next player. 

The key mechanic is that after rolling two pigs, players can choose to:
- **Bank their points**: Add turn score to total and pass to next player
- **Roll again**: Risk their current turn score for a chance at more points

Penalties can occur that either end the turn (Pig Out), reset total score (Oinker), or eliminate the player (Piggyback). Double bonuses are awarded when both pigs land in identical positions. First player to reach 100 points wins.

## Core Features

### 1. Scoring System
The app must support all official Pass the Pigs scoring positions:

#### Basic Combinations:
- **Sider**: Both pigs land on the same side (both spot up or both spot down) - 1 point
- **Pig Out**: Pigs land on opposite sides (one spot up, one spot down) - 0 points, turn ends
- **Trotter**: One pig stands on all four feet, the other on its side - 5 points
- **Razorback**: One pig lands on its back, the other on its side - 5 points
- **Snouter**: One pig balances on its snout and two front feet, the other on its side - 10 points
- **Leaning Jowler**: One pig balances on snout, ear, and one foot, the other on its side - 15 points

#### Double Positions (both pigs same):
- **Double Trotter**: Both pigs stand on all four feet - 20 points
- **Double Razorback**: Both pigs land on their backs - 20 points
- **Double Snouter**: Both pigs balance on snout and two front feet - 40 points
- **Double Leaning Jowler**: Both pigs balance on snout, ear, and one foot - 60 points

#### Mixed Combinations:
- **Mixed Combo**: Any mix of Trotter, Razorback, Snouter, or Leaning Jowler - Sum of individual scores

#### Penalty Positions:
- **Oinker**: Pigs touch each other in any position - Lose all accumulated points
- **Piggyback**: One pig lands on top of the other, not touching the table - Player eliminated from the game

### 2. User Interface Design

#### Main Game Screen:
- **Current Player Indicator**: Large, prominent display of whose turn it is
- **Score Display**: 
  - Current turn score (points not yet banked)
  - Total game scores for all players
  - Visual progress bars showing proximity to 100 points
- **Scoring Buttons**: Large, easily tappable buttons with:
  - Clear pig position icons/illustrations
  - Point values prominently displayed
  - Color-coded for quick identification
- **Action Buttons**:
  - "Bank Points" - Add turn score to total and pass turn
  - "New Game" - Reset all scores
  - "Undo Last Roll" - Reverse last scoring action

#### Scoring Button Layout (3x3 Grid):
```
[Dot Sider: 1pt]     [Sider: 0pts]       [Trotter: 5pts]
[Razorback: 5pts]    [Snouter: 10pts]    [Leaning Jowler: 15pts]
[Pig Out: Turn Ends] [Oinker: Score→0]   [Piggyback: Eliminated]
```

Note: Double bonuses (Double Trotter: 20pts, Double Razorback: 20pts, Double Snouter: 40pts, Double Leaning Jowler: 60pts) are automatically calculated when both pigs land in the same position. Mixed combinations automatically sum individual scores.

### 3. Player Management

#### Quick Player Setup:
- **Preset Player Options**: "Player 1", "Player 2", etc. for immediate start
- **Smart Defaults**: 2-player game as default setup
- **Quick Add**: Single tap to add additional players (up to 6 total)

#### Custom Player Names:
- **Optional Naming**: Tap on any player name to edit
- **Keyboard Input**: Standard text input with auto-capitalization
- **Name Validation**: Prevent duplicate names, handle empty names gracefully

#### Player List Features:
- **Drag to Reorder**: Change play order before game starts
- **Swipe to Remove**: Easy player removal
- **Visual Indicators**: Show active player, eliminated players

### 4. Smart Turn Management

#### Automatic Turn Switching:
- Auto-advance to next player when "Bank Points" pressed
- Auto-advance on penalty rolls (Pig Out, Oinker)
- Skip eliminated players automatically

#### Turn Indicators:
- **Active Player Highlighting**: Clear visual emphasis on current player
- **Turn Counter**: Show current round number
- **Next Player Preview**: Show who's up next

### 5. Game State Management

#### Score Tracking:
- **Persistent Scores**: Maintain scores during app backgrounding
- **Game History**: Track individual turns and scoring events
- **Statistics**: Track game statistics (optional)

#### Game Controls:
- **Pause/Resume**: Handle app lifecycle gracefully
- **Game Reset**: Clear confirmation dialog
- **Settings**: Basic app preferences

## Technical Requirements

### Platform Support:
- **iOS**: iOS 14.0+
- **Android**: API Level 24+ (Android 7.0)

### Architecture:
- **Kotlin Multiplatform**: Shared business logic
- **Compose Multiplatform**: Shared UI components
- **Local Storage**: Game state persistence
- **MVVM Pattern**: Clean architecture with ViewModels

### Performance:
- **Responsive UI**: <100ms button response time
- **Memory Efficient**: Minimal memory footprint
- **Battery Optimized**: Efficient background handling

## User Experience Guidelines

### Accessibility:
- **Large Touch Targets**: Minimum 44dp/44pt button size
- **High Contrast**: Clear visual differentiation
- **Screen Reader Support**: Proper content descriptions
- **Font Scaling**: Support system font size preferences

### Visual Design:
- **Material Design 3** (Android) / **Human Interface Guidelines** (iOS)
- **Consistent Color Scheme**: Easy to identify scoring categories
- **Clear Typography**: Readable at all system font sizes
- **Intuitive Icons**: Self-explanatory pig position illustrations

### Interaction Design:
- **One-Handed Use**: Primary controls within thumb reach
- **Confirmation Dialogs**: For destructive actions only
- **Haptic Feedback**: Subtle feedback on button presses
- **Error Prevention**: Disable invalid actions rather than showing errors

## Optional Advanced Features

### Enhanced Gameplay:
- **Hog Call**: Optional advanced rule implementation
- **Tournament Mode**: Multi-game tournaments with standings
- **Custom Rules**: Adjustable winning score, house rules

### Social Features:
- **Game Sharing**: Share final scores to social media
- **Player Profiles**: Save frequent players with stats
- **Achievement System**: Fun milestones and badges

### Quality of Life:
- **Sound Effects**: Optional pig-themed sound effects
- **Animations**: Subtle scoring animations
- **Themes**: Light/dark mode, color customization
- **Statistics**: Detailed game analytics and history

## Success Metrics
- **Usability**: New users can start a game within 30 seconds
- **Accuracy**: Zero scoring calculation errors
- **Reliability**: <1% crash rate across all sessions
- **Performance**: App launches in <3 seconds on target devices

## Future Considerations
- **Multiplayer**: Online multiplayer functionality
- **AI Players**: Computer opponents with difficulty levels
- **Rules Engine**: Support for other pig dice variants
- **Integration**: Connect with physical smart dice (if available)