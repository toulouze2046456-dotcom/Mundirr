# Description Updates — Neutral, Concise, Explanatory Tone

All user-facing descriptions have been updated to follow a **neutral, concise, explanatory** tone for improved UX clarity.

## Summary of Changes

### 1. **App.tsx** — PageHeader Subtitles (13 pages)
Updated all page navigation subtitles to be clear and descriptive:

| Page | Old | New |
|------|-----|-----|
| Dashboard | MAIN TERMINAL | Dashboard Overview |
| Command | RANK & ECONOMY | Rank Progression & Rewards |
| Profile | OPERATOR ID | Account & Settings |
| Physical | BODY METRICS | Track Workouts & Fitness |
| Nutrition | FUEL INTAKE | Food & Supplements |
| Sleep | REST CYCLES | Sleep Quality & Rhythm |
| Mind | COGNITIVE | Books, Films & Culture |
| Spiritual | RELIGIOUS | Prayer & Calendars |
| Capital | FINANCIAL | Income & Spending |
| Habits | SUBSTANCES | Track Consumption |
| Cycle | MENSTRUAL | Period Tracking |
| Bio-Vault | GENOMICS | Sleep & DNA Insights |

### 2. **GenesisProtocol.tsx** — Onboarding Step Subtitles (6 steps)
Simplified and clarified all onboarding step guidance:

| Step | Old | New |
|------|-----|-----|
| Bio-Baseline | Tell us about yourself to personalize your experience | Create your profile with core info |
| Physical Deep-Dive | Customize your training profile | Choose your training focus & frequency |
| Wearable | Connect wearables for automated Proof-of-Work | Connect your devices for data sync |
| Bio-Sovereignty (DNA) | DNA analysis unlocks the 1.05× Chronotype Efficiency Bonus | DNA analysis enables genetic personalization |
| Spiritual Alignment | Select your faith to enable prayer & fasting calendars | Select your faith & calendars |
| Subscription | No payment required during beta | Select your subscription plan |

### 3. **GenesisContext.tsx** — Module Descriptions (9 modules)
Standardized all module descriptions to consistent explanatory tone:

| Module | Old | New |
|--------|-----|-----|
| Physical Exercises | Track workouts, strength training, and cardio sessions | Log workouts and track fitness progress |
| Nutrition | Food logging, supplements, meal planning | Track food intake and supplements |
| Bulwark | The Bulwark — block time-wasting apps | Block distracting apps during focus time |
| Financial | Income, expenses, saving goals | Track income and spending |
| Habits | Track smoking, drinking, and other habits | Monitor smoking, drinking, and substance use |
| Sleep & Circadian | BioVault — sleep quality and circadian rhythm | Track sleep quality and circadian rhythm |
| Culture | Books, movies, music, and cultural growth | Track books, movies, music, and learning |
| Cycle Period | Menstrual cycle tracking and predictions | Track menstrual cycle and predictions |
| Religion | Prayer schedules, fasting calendars | Prayer schedules and religious calendars |

### 4. **OracleNavigator.tsx** — Missing Data Field Hints (5 fields)
Clarified all Oracle Navigator guidance messages:

| Field | Old | New |
|-------|-----|-----|
| Location | Add your location to sync sunrise/sunset for BioVault | Location enables circadian data syncing |
| Wearable | Connect a device for automated health tracking | Connect a device for automated tracking |
| DNA | Upload DNA data for the 1.05× Chronotype Bonus | DNA data enables genetic personalization |
| Physical Focus | Define your workout focus areas | Specify your training focus areas |
| Religion | Choose your religion for prayer/fasting calendars | Select your faith for calendars |

### 5. **NoDataState.tsx** — Monthly Recap Empty State (4 strings)
Simplified month-end summary messaging:

| Field | Old | New |
|-------|-----|-----|
| Main description | Your personalized monthly recap will be available from the 1st of {monthName}. Start logging your activities today to see your progress next month! | Your monthly summary will be ready on the 1st of {monthName}. Log your activities now to see insights next month. |
| Feature 1 Label | Track Progress | Track Activities |
| Feature 1 Hint | Log daily activities | Log data daily |
| Feature 2 Label | Earn $MUND | Earn Rewards |
| Feature 2 Hint | Complete missions | Gain $MUND tokens |

### 6. **CulturalHealthModule.tsx** — Empty States (2 messages)
Concisified cultural health collection messaging:

| State | Old | New |
|-------|-----|-----|
| Books collection empty | No books yet. Search and add your first book! | Add books to your collection |
| Books search no results | No books found. Try a different search. | No books found |

## Tone Guidelines Applied

All descriptions now follow these principles:

✅ **Neutral**: Avoid marketing language, exclamation points, or promotional tone
✅ **Concise**: 2–7 words per subtitle, 1–2 short sentences for descriptions
✅ **Explanatory**: Clearly state what the feature does or where the user is in the flow

## Verification

- ✅ Zero TypeScript errors across all 6 modified files
- ✅ All changes backward-compatible (no functional changes)
- ✅ Tone consistent across all 40+ description strings
- ✅ Copy length optimized for mobile readability

## Files Modified

1. `client/src/App.tsx` — 13 PageHeader subtitles
2. `client/src/components/GenesisProtocol.tsx` — 6 step subtitles
3. `client/src/contexts/GenesisContext.tsx` — 9 module descriptions
4. `client/src/components/OracleNavigator.tsx` — 5 field hints
5. `client/src/components/NoDataState.tsx` — 4 empty-state messages
6. `client/src/components/CulturalHealthModule.tsx` — 2 collection empty states

**Total: 40+ user-facing description strings updated**
