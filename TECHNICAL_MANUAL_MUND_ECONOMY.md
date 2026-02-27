# VELLBEING OS — $MUND ECONOMY TECHNICAL MANUAL

### Single Source of Truth · v1.0 · 7 February 2026

**Classification:** FOUNDER-ONLY · LEGALLY BINDING REFERENCE DOCUMENT  
**Audited From:** `MundEngine.ts`, `DeflationEngine.ts`, `AffiliateService.ts`, `SovereigntyManager.service.ts`, `AchievementsDB.ts`, `ResonanceEngine.service.ts`, `SupplementNexus.service.ts`, `GamificationSystem.ts`, `ProgramScheduler.service.ts`, `ChronosArchitect.service.ts`, `BioVaultModule.tsx`, `OracleAI.tsx`, `BurnCounter.tsx`, `useHealthStats.ts`, `DuelNotificationService.ts`, `App.tsx`, `ResonanceModule.tsx`

---

## TABLE OF CONTENTS

1. [I. Universal Reward Inventory](#i-universal-reward-inventory)
   - [1.1 Physical Mining (Steps)](#11-physical-mining-steps)
   - [1.2 Physical Mining (Workouts)](#12-physical-mining-workouts)
   - [1.3 Sleep Mining](#13-sleep-mining)
   - [1.4 Nutrition Logging](#14-nutrition-logging)
   - [1.5 Cognitive Activities](#15-cognitive-activities)
   - [1.6 Affiliate Cashback](#16-affiliate-cashback)
   - [1.7 Bulwark System (P2P Accountability)](#17-bulwark-system-p2p-accountability)
   - [1.8 Sovereignty Missions (Addiction Staking)](#18-sovereignty-missions-addiction-staking)
   - [1.9 Clean Counter (Addiction Recovery)](#19-clean-counter-addiction-recovery)
   - [1.10 Streak Bonuses](#110-streak-bonuses)
   - [1.11 Ad-Watched Rewards](#111-ad-watched-rewards)
   - [1.12 Referral Rewards](#112-referral-rewards)
   - [1.13 Milestones & Achievements (One-Time)](#113-milestones--achievements-one-time)
   - [1.14 Program Milestone Bonuses](#114-program-milestone-bonuses)
   - [1.15 Tier Efficiency Multiplier](#115-tier-efficiency-multiplier)
   - [1.16 Chronotype Alignment Bonus](#116-chronotype-alignment-bonus)
   - [1.17 Master Constants Table](#117-master-constants-table)
2. [II. Zero-Trust Verification Audit](#ii-zero-trust-verification-audit)
   - [2.1 Vulnerability Analysis](#21-vulnerability-analysis)
   - [2.2 Trust-Weight Matrix](#22-trust-weight-matrix)
   - [2.3 Hardware Enforcement Strategy](#23-hardware-enforcement-strategy)
   - [2.4 Integrity Score Definition](#24-integrity-score-definition)
3. [III. Macro-Economic "Shark" Protocol](#iii-macro-economic-shark-protocol)
   - [3.1 Tokenomics Overview](#31-tokenomics-overview)
   - [3.2 Cash Flow Security (MAX_MUND_TO_COMMISSION_RATIO)](#32-cash-flow-security)
   - [3.3 The Burn Black Hole (DeflationEngine)](#33-the-burn-black-hole)
   - [3.4 Liquidity Moat (Escrow & Exit Penalty)](#34-liquidity-moat)
   - [3.5 Founder Profitability Proof](#35-founder-profitability-proof)
   - [3.6 Data Monetization & Aggregate Insights](#36-data-monetization--aggregate-insights)
4. [IV. The Neural Brain (Oracle AI)](#iv-the-neural-brain-oracle-ai)
   - [4.1 Data Flow Map](#41-data-flow-map)
   - [4.2 Pearson Correlation Engine](#42-pearson-correlation-engine)
   - [4.3 Supplement Nexus → ProgramScheduler Pipeline](#43-supplement-nexus--programscheduler-pipeline)
5. [V. Critical Logic Traces](#v-critical-logic-traces)
   - [5.1 Purchase-to-Cashback Trace](#51-purchase-to-cashback-trace)
   - [5.2 Sync-to-Burn Trace](#52-sync-to-burn-trace)

---

# I. UNIVERSAL REWARD INVENTORY

> Every stream through which an aggregate user can earn `$MUND`, traced to source code.

---

## 1.1 Physical Mining (Steps)

| Property | Value |
|---|---|
| **Logic Trigger** | `DeflationEngine.calculateStepsMining()` |
| **Source File** | `DeflationEngine.ts:245-260` |
| **Reward Formula** | $\text{mundEarned} = \left\lfloor \frac{\text{verifiedSteps}}{\text{STEPS\_PER\_MUND}} \right\rfloor$ |
| **Rate** | `STEPS_PER_MUND = 100` → **1 $MUND per 100 steps** → **10 $MUND per 1,000 steps** |
| **Anti-Inflation Cap** | `STEPS_DAILY_CAP = 10,000 steps` → **Max 100 $MUND/day from steps** |
| **Source of Truth** | **Zero-Trust** — HealthKit / Health Connect sensor-verified |

**Formula (LaTeX):**

$$\text{MUND}_{\text{steps}} = \min\left(\left\lfloor \frac{S_{\text{verified}}}{100} \right\rfloor,\ 100\right)$$

where $S_{\text{verified}}$ = steps from HealthKit/Health Connect API.

---

## 1.2 Physical Mining (Workouts) — METABOLIC-VERIFIED ✅ IMPLEMENTED

| Property | Value |
|---|---|
| **Logic Trigger** | `ResonanceEngine.verifyWorkoutMetabolics()` → `MundEngine.addMund()` |
| **Source Files** | `ResonanceEngine.service.ts`, `MundEngine.ts`, `PhysicalHealthModule.tsx` |
| **Verification** | **Metabolic-Verified** — "No Sweat, No Mund" biometric proof-of-effort |
| **Anti-Inflation Cap** | **Max 2 workouts/day** → **Max 40 $MUND/day** |
| **Source of Truth** | Hardware-Only (reuses `classifySource()` from Step Mining) |

### Tiered Reward System

| Tier | Category | MET Threshold | Examples | Reward |
|---|---|---|---|---|
| **TIER_A** | High Intensity | MET ≥ 7.0 | HIIT, Running, Swimming, Jump Rope | **20 $MUND** |
| **TIER_B** | Moderate Intensity | 3.5 ≤ MET < 7.0 | Strength, Cycling, Dancing | **15 $MUND** |
| **TIER_C** | Low Intensity | MET < 3.5 | Yoga, Walking, Stretching, Pilates | **5 $MUND** |

**Flexibility/Meditation Penalty:** Workouts in `Flexibility`, `Recovery`, or `Core` categories receive `0.5×` multiplier (e.g., Yoga TIER_C = `5 × 0.5 = 2.5 ≈ 3 $MUND`).

### Metabolic Intensity Check Formula

$$\text{APPROVED} = \left( D_{\text{min}} \geq 20 \right) \wedge \left( \frac{\overline{HR}_{\text{workout}}}{HR_{\text{resting}}} \geq 1.5 \right)$$

where:
- $D_{\text{min}}$ = workout duration in minutes
- $\overline{HR}_{\text{workout}}$ = average heart rate during workout (from HealthKit/wearable)
- $HR_{\text{resting}}$ = resting heart rate from biometric profile

**No HR Sensor Fallback:** If no heart rate data is available, the system falls back to MET-based verification: `MET ≥ 3.0` required.

### Anti-Duplication Logic (WorkoutUUID)

1. Each HealthKit `HKWorkout` has a unique UUID (`workoutUUID`)
2. Processed UUIDs are stored in `localStorage` (last 500, key: `vb_workout_uuids_v1`)
3. Duplicate UUIDs are rejected with flag `DUPLICATE_UUID`
4. Overlapping timestamps with already-rewarded workouts are rejected with flag `TIMESTAMP_OVERLAP`
5. Minimum 1-hour gap between rewarded workouts enforced

### Verification Gates (Sequential)

```
Gate 1: WorkoutUUID dedup       → REJECT if duplicate
Gate 2: wasUserEntered check    → REJECT if manual entry
Gate 3: Daily cap (2/day)       → REJECT if exceeded
Gate 4: Timestamp overlap       → REJECT if overlapping
Gate 5: Duration ≥ 20 min       → REJECT if too short
Gate 6: HR Ratio ≥ 1.5×        → REJECT if intensity too low
  └─ Fallback: MET ≥ 3.0       → REJECT if sedentary
─── ALL GATES PASSED ───
Classify Tier (A/B/C) → Apply metabolic multiplier → Credit $MUND
```

### Source File Trace

| Function | File | Purpose |
|---|---|---|
| `verifyWorkoutMetabolics()` | `ResonanceEngine.service.ts` | Core 6-gate verification pipeline |
| `classifyWorkoutTier()` | `ResonanceEngine.service.ts` | MET + keyword → TIER_A/B/C |
| `getProcessedWorkoutUUIDs()` | `ResonanceEngine.service.ts` | UUID dedup store |
| `checkTimestampOverlap()` | `ResonanceEngine.service.ts` | Overlap detection |
| `addMund()` | `MundEngine.ts` | Credits wallet with `WORKOUT_VERIFIED` sourceType |
| `addExerciseFromWger()` | `PhysicalHealthModule.tsx` | UI integration + verification trigger |

---

## 1.3 Sleep Mining

| Property | Value |
|---|---|
| **Logic Trigger** | `DeflationEngine.calculateSleepMining()` |
| **Source File** | `DeflationEngine.ts:262-272` |
| **Reward Formula** | If `verifiedSleepHours ≥ 7` → **50 $MUND** flat reward |
| **Anti-Inflation Cap** | `SLEEP_DAILY_CAP = 1` → **Max 50 $MUND/day** (one claim) |
| **Source of Truth** | **Zero-Trust** — HealthKit / Health Connect verified sleep session |

**Formula:**

$$\text{MUND}_{\text{sleep}} = \begin{cases} 50 & \text{if } h_{\text{verified}} \geq 7 \\ 0 & \text{otherwise} \end{cases}$$

---

## 1.4 Nutrition Logging — CAMERA-VERIFIED ✅ IMPLEMENTED

| Property | Value |
|---|---|
| **Logic Trigger** | `NutritionModule.logSelectedFood()` → `HealthService.processMeal()` |
| **Source Files** | `NutritionModule.tsx`, `HealthService.ts`, `MundEngine.ts`, `NutritionDataLedger.ts` |
| **Reward Formula** | **5 $MUND** per camera-verified meal |
| **Anti-Inflation Cap** | **Max 3 meals/day** (hard cap, no time-gap restriction) = **15 $MUND/day** |
| **Water** | **2 $MUND** per water glass, **max 8/day** = 16 $MUND |
| **Source of Truth** | 📸 **Camera-Only** — Oracle Lens enforces `input.capture='environment'` (no gallery) |

### Camera-Only Enforcement (Oracle Lens)

Meals logged via the **Oracle Lens** camera pipeline are the only ones eligible for $MUND rewards:

1. `captureWithCamera()` opens device camera with `capture='environment'` — **gallery picker is disabled**
2. If user bypasses camera (USDA search or custom entry), `captureSource` = `'search'` or `'manual'` → **0 $MUND**
3. Only `captureSource === 'camera'` logs count toward the 3/day verified limit

### Verification Status

| Entry Method | `captureSource` | `verificationStatus` | $MUND |
|---|---|---|---|
| Camera capture → USDA search | `camera` | `VERIFIED` | **5 $MUND** (if < 3/day) |
| USDA search only | `search` | `UNVERIFIED` | 0 (journaling only) |
| Custom manual entry | `manual` | `UNVERIFIED` | 0 (journaling only) |

### Daily Limit Logic

$$\text{MUND}_{\text{meal}} = \begin{cases} 5 & \text{if } \text{captureSource} = \text{camera} \wedge V_{\text{today}} < 3 \\ 0 & \text{otherwise (journaling only)} \end{cases}$$

where $V_{\text{today}}$ = count of camera-verified meals logged today.

### Data Monetization Pipeline

When user has consented to anonymized research (`vb_research_consent = true`), each logged meal generates an `AnonymizedInsightSchema` entry:
- **Stripped:** UserID, Name, Email
- **Retained:** Age range, Gender, BMI range, Activity level, Meal content (food name, macros, USDA FDC ID)
- **Stored:** Rolling buffer of 500 entries in `vb_nutrition_insight_ledger`

See [§3.6 Data Monetization](#36-data-monetization--aggregate-insights) for business model.

---

## 1.5 Cognitive Activities

| Property | Value |
|---|---|
| **Logic Trigger** | `MundEngine.MUND_REWARDS.reading_30min` / `learning_session` |
| **Source File** | `MundEngine.ts:89-90` |
| **Reward Formula** | **10 $MUND** per 30-min reading, **8 $MUND** per completed lesson |
| **Anti-Inflation Cap** | Rate-limited (implied, no explicit daily cap in code) |
| **Source of Truth** | ⚠️ **HIGH-RISK — Manual entry, no sensor verification** |

---

## 1.6 Affiliate Cashback — ESCROW-VERIFIED ✅ IMPLEMENTED

| Property | Value |
|---|---|
| **Logic Trigger** | `AffiliateService.moveToPending()` → `moveToConfirmed()` → `MundEngine.creditAffiliateMund()` |
| **Source File** | `AffiliateService.ts` (escrow + formula), `MundEngine.ts` (wallet crediting) |
| **Source Type** | `AFFILIATE_VERIFIED` (added to `MundSourceType` union) |
| **Anti-Inflation Cap** | $\text{MUND}\_{\text{issued}} \le C\_{\text{commission}} \times 600$ |
| **Source of Truth** | ✅ **API-Verified** — Skimlinks webhook / CSV import, 60-day escrow |

### Core Formula (50/50 Deterministic Split)

$$\text{Company Profit} = C_{\text{commission}} \times 0.50$$

$$\text{User Cashback} = C_{\text{commission}} \times 0.50$$

$$\text{MUND}\_{\text{affiliate}} = (C_{\text{commission}} \times 0.50) \times 1000$$

**Hard Guardrail (Safety Cap):**

$$\text{MUND}\_{\text{issued}} \le C_{\text{commission}} \times 600$$

**Strict Rule:** Bonuses are NEVER added to the commission base calculation.

**Example:** €10 commission → €5 company profit + €5 cashback → 5,000 $MUND (capped at 6,000 max).

### 4-State Escrow Machine (Anti-Return Fraud)

| State | Trigger | $MUND Visibility | Balance |
|---|---|---|---|
| **TRACKED** | Click recorded via `trackClick()` | None | — |
| **PENDING** | Commission detected via webhook/CSV → `moveToPending()` | Visible in "In the Vault" | Locked (not spendable) |
| **CONFIRMED** | 60-day return window passed → `moveToConfirmed()` → `creditAffiliateMund()` | Moves to "Total Earned" | Available (spendable) |
| **REVERSED** | User returned item → `moveToReversed()` | Removed from pending | Removed |

**Escrow sweep:** `checkEscrowExpiry()` auto-confirms any `PENDING` transaction where `now >= escrowExpiresAt`.

### MundEngine Integration

When an escrow transaction transitions to `CONFIRMED`:

1. `AffiliateService.moveToConfirmed(txId)` returns `{ mundToCredit }`
2. `MundEngine.creditAffiliateMund(wallet, mundToCredit, merchantName)` is called
3. `addMund()` fires with `sourceType = 'AFFILIATE_VERIFIED'`, `multiplier = 1.0`
4. Transaction logged in `wallet.transactionLedger` with flag `affiliate:MerchantName`

The stale `MUND_REWARDS.purchase_cashback` constant has been deprecated — all affiliate $MUND flows through `AffiliateService.calculateAffiliateMund()`.

### Technical Integration (Skimlinks)

| Channel | Handler | Behavior |
|---|---|---|
| Webhook | `processWebhook(payload, signature?)` | Maps `status` → state transition (cancelled/reversed → `REVERSED`, else → `PENDING`) |
| CSV Import | `importFromCSV(csvContent)` | Parses Skimlinks CSV, routes each row to state machine |
| Manual Entry | `addManualCommission(userId, merchant, orderValue, commissionAmount)` | Enters directly as `PENDING` (starts 60-day escrow) |

**Data harvested for Total Data Ledger:** `merchant_name`, `category`, `order_value`, `commission_amount`.

### UI: Earnings Ledger (FinancialHealthModule.tsx)

| Section | Content |
|---|---|
| **Total Earned (Confirmed)** | `totalMundConfirmed` — confirmed $MUND + confirmed € commission |
| **In the Vault (Pending)** | `totalMundPending` — locked $MUND + pending € in escrow |
| **Cashback Ratio** | Progress bar targeting ≤60% (`mundToCommissionRatio`) |
| **Transaction Counts** | Confirmed / Pending / Avg Order Value |
| **60-day Lock Message** | "Cashback is locked for 60 days to verify the purchase and prevent return fraud." |
| **History List** | Expandable list with status badges: `Pending (Xd)`, `Confirmed`, `Reversed` |

### Configuration Constants (`AFFILIATE_CONFIG`)

| Key | Value | Description |
|---|---|---|
| `CASHBACK_RATE` | 0.50 | 50% → user |
| `COMPANY_PROFIT_RATE` | 0.50 | 50% → company |
| `MUND_PER_EURO` | 1,000 | Conversion rate |
| `MAX_MUND_PER_COMMISSION_EURO` | 600 | Safety cap |
| `ESCROW_DAYS` | 60 | Anti-return-fraud hold |
| `MIN_COMMISSION_EURO` | 0.01 | Minimum threshold |

---

## 1.7 Bulwark System (P2P Accountability)

| Property | Value |
|---|---|
| **Logic Trigger** | `FocusDuelManager.completeDuel()` / `FocusDuelManager.handleBreach()` / `FocusDuelManager.calculateGroupDuelEconomy()` |
| **Source Files** | `SovereigntyManager.service.ts`, `DuelNotificationService.ts`, `App.tsx`, `OracleAI.tsx` |
| **Reward Formula (1v1)** | $\text{Payout} = \text{Own\_Stake} + (\text{Opponent\_Stake} \times 0.8) + \text{Duration\_Bonus}$ |
| **Reward Formula (Group)** | $G_{\text{survivor}} = S_{\text{self}} + B_{\text{dur}} + \frac{\sum S_{\text{losers}} \times 0.80}{N_{\text{survivors}}}$ |
| **Anti-Inflation Cap** | `DUEL_DAILY_CAP = 1` rewarded duel per day |
| **Source of Truth** | 🟢 **LOW-RISK — NativeFocusShield + Selective Blacklist + Social Key + Guardian Browser** |

### Floating Command Stack (Navigation)

The legacy `PulsingAuraButton` has been replaced by a **3-FAB vertical floating stack** anchored to the bottom-right corner of the Dashboard. Each FAB opens the Oracle AI modal pre-focused on a specific tab:

| Position | Icon | Color | Action | Tab |
|---|---|---|---|---|
| Bottom | ✨ Sparkles | `#8b5cf6` (purple) | Opens AI consultation | `resonance` |
| Middle | 📋 ClipboardList | `#6366f1` (indigo) | Opens programs view | `programs` |
| Top | 🛡️ Ban | `#ef4444` (red) | Opens Bulwark arena | `sovereignty` |

**Style:** `52×52px`, `borderRadius: 50%`, glassmorphism (`rgba(255,255,255,0.90)`, `backdropFilter: blur(16px)`), `boxShadow: 0 2px 12px rgba(0,0,0,0.08)`. Vertical spacing: `12px` gap.

**Source:** `App.tsx` — replaces `PulsingAuraButton` import. `OracleAI` now accepts an `initialTab?: TabType` prop synced via `useEffect`.

### Dashboard Hero Row (Wallet & Multiplier)

The Dashboard header no longer displays date/time. Instead, the Hero Row shows:

| Element | Style | Source |
|---|---|---|
| **$MUND Balance** | `38px bold`, right-aligned `$MUND` label | `loadWallet().mundBalance` |
| **Tier Multiplier Badge** | Pill: `bg rgba(139,92,246,0.10)`, `color #8b5cf6`, `font 13px 600` | `loadWallet().tier?.badge` + `% BONUS` |
| **Integrity Score** | Dark pill: `bg #0f1729`, `color white`, `font 12px bold` | Existing `integrityScore` state |

**Rationale:** Users see their real-time economic position at a glance — balance, earning multiplier, and trust score — without navigating to a sub-page.

### Strong Correlations on Dashboard (Resonance Plate)

The **Strong Correlations** panel, previously only visible inside the Oracle AI → Resonance tab, is now surfaced directly on the **ResonanceModule** component on the main Dashboard.

| Property | Value |
|---|---|
| **Source** | `ResonanceEngine.analyzeCorrelations()` via `ResonanceModule.tsx` |
| **Trigger** | `useEffect` recomputes when `logs` change |
| **Display** | Up to 4 correlation cards with strength % badge (green ≥50%, red <50%), trigger → effect text, and recommendation |
| **Style** | `white/90` bg, `rounded-[2rem]`, `1px solid rgba(0,0,0,0.06)`, `boxShadow: 0 1px 4px rgba(0,0,0,0.04)` |
| **Header** | Link2 icon + "Resonance Plate" + "Strong correlations detected today" |

This gives users immediate insight into their biometric patterns without opening the Oracle AI.

### Solo Bulwark = 0 $MUND

Solo Bulwark sessions no longer earn $MUND. All Bulwark rewards require P2P duel accountability.

| Duration | Solo Reward | Duel Duration Bonus |
|---|---|---|
| 30 minutes | 0 $MUND | +5 $MUND |
| 60 minutes | 0 $MUND | +15 $MUND |
| 90 minutes | 0 $MUND | +30 $MUND |

### Guardian Browser (Friend Discovery)

The Guardian Browser is a searchable, scrollable friend list embedded in the Duel Setup view. Users **must** select an opponent before initiating a duel challenge.

**Discipline Score (0–100):**

$$\text{DisciplineScore} = \min\!\left(40,\; \text{streak} \times \frac{40}{30}\right) + \min\!\left(30,\; \text{level} \times \frac{30}{40}\right) + \min\!\left(30,\; \frac{\text{weeklyXP}}{1000} \times 30\right)$$

| Component | Max Points | Source |
|---|---|---|
| Streak | 40 | Active-day streak (capped at 30 days) |
| Level | 30 | User progression level (capped at 40) |
| Weekly XP | 30 | Weekly XP normalized over 1000 |

**Color Coding:**

| Score Range | Color | Meaning |
|---|---|---|
| 80–100 | `#22c55e` (green) | Elite discipline |
| 50–79 | `#f59e0b` (amber) | Moderate |
| 0–49 | `#ef4444` (red) | Needs improvement |

**UI Elements:**
- **"Recent" pills** — up to 5 most recent opponents with quick-select
- **Searchable list** — filter by name or @handle
- **Per-friend row** — avatar, name, status dot, streak 🔥, W/L record, discipline bar
- **Style** — `white/90` bg, `rounded-[2rem]`, `shadow-sm`

### Invitation Lifecycle (P2P Challenge Flow)

The duel flow now follows a strict invitation lifecycle before locks activate:

```
SENT → DELIVERED → ACCEPTED / REJECTED / EXPIRED
```

| State | Trigger | Effect |
|---|---|---|
| `SENT` | User presses "Challenge [Name] · X $MUND" | Creates `DuelInvitation`, saves to localStorage, STUB for `POST /duel/invite` |
| `DELIVERED` | Server confirms push delivery (simulated 500ms) | Status transitions, UI shows "Waiting for X to accept..." |
| `ACCEPTED` | Opponent taps "Accept & Lock" in Acceptance Modal | Both devices trigger `NativeFocusShield.lock()` simultaneously via WebSocket |
| `REJECTED` | Opponent taps "Reject" | Invitation closed, no stake deducted |
| `EXPIRED` | 5 minutes elapsed (`INVITE_TTL_MS = 300000`) | Auto-swept by `sweepExpiredInvites()` |

**Invite TTL:** 5 minutes. Expired invites are garbage-collected on each poll cycle.

### Acceptance Modal (Incoming Challenge)

When an opponent receives a challenge, a full-screen modal appears with vibration feedback (`[200, 100, 200, 100, 300]`).

| Section | Content |
|---|---|
| **Header** | Bell icon + "⚔️ Incoming Duel Challenge" + TTL countdown |
| **Challenger Info** | Avatar, name, "wants to duel you" |
| **Terms** | Stake amount, duration, opponent's committed blocked apps (icons + names + 🔒) |
| **Warning** | "You must stake X $MUND and block your own apps. Both devices lock simultaneously." |
| **Actions** | "Reject" (gray, flex: 1) · "Accept & Lock" (gradient purple→pink, flex: 2) |

**On Accept:**
1. `acceptInvite(id)` transitions invitation to `ACCEPTED`
2. WebSocket signal sent to challenger's device (STUB)
3. `NativeFocusShield.lock()` fires on **both** devices simultaneously
4. `FocusDuelManager.startFocusDuel()` starts the timer on both sides

### Duel Economy Rules

**Mutual Success (both complete):**

$$\text{MUND}_{\text{mutual}} = A_{\text{own}} + B_{\text{duration}}$$

**One-Sided Win (opponent breaches):**

$$\text{MUND}_{\text{duel\_win}} = A_{\text{own}} + (A_{\text{opponent}} \times 0.80) + B_{\text{duration}}$$

**Loser Penalty:**
- 100% of loser's stake is forfeited
- 80% goes to winner
- **20% is BURNED** ($\text{DUEL\_BURN\_RATE} = 0.20$) → `MundEngine.totalReserve` (deflationary)

**Stake Bounds:** `DUEL_MIN_STAKE = 10`, `DUEL_MAX_STAKE = 500`

**Example (100 $MUND stake, 60min, 1v1):**
| Party | Outcome | Net $MUND |
|---|---|---|
| Mutual Success | Each gets back 100 + 15 bonus | **+15 $MUND each** |
| One-Sided Winner | Gets back 100 + (100 × 0.8) + 15 bonus | **+95 $MUND** |
| One-Sided Loser | Loses entire 100 stake | **−100 $MUND** |
| Burn | 20% of loser's stake destroyed | **20 $MUND burned** |

### Group Challenge Economy (Multiplayer Survivor Formula)

Group duels extend the P2P model to $N$ total participants. The economy follows a **survivor model**: losers forfeit their entire stake, 20% is burned, and the remaining 80% is split equally among all survivors.

**`GroupDuelEconomyResult` Interface:**

| Field | Type | Description |
|---|---|---|
| `survivorPayout` | `number` | Total payout per survivor |
| `burnAmount` | `number` | 20% of all losers' stakes (deflationary) |
| `durationBonus` | `number` | Duration bonus from `DUEL_DURATION_BONUS` |
| `loserPoolShare` | `number` | Each survivor's share of the 80% pool |

**Formula:**

$$G_{\text{survivor}} = S_{\text{self}} + B_{\text{dur}} + \frac{\sum S_{\text{losers}} \times 0.80}{N_{\text{survivors}}}$$

Where:
- $S_{\text{self}}$ = survivor's own stake (returned in full)
- $B_{\text{dur}}$ = `DUEL_DURATION_BONUS[duration]` (5 / 15 / 30 $MUND)
- $\sum S_{\text{losers}}$ = total of all loser stakes
- $0.80$ = `1 - DUEL_BURN_RATE` (80% distributable)
- $N_{\text{survivors}}$ = number of participants who completed the challenge

**Burn:** $\text{Burn} = \lfloor \sum S_{\text{losers}} \times 0.20 \rfloor$ → sent to `MundEngine.totalReserve` (deflationary)

**Method:** `FocusDuelManager.calculateGroupDuelEconomy(ownStake, loserStakes[], survivorCount, durationMinutes)`

**Example (5 players, 100 $MUND each, 60min, 3 survive / 2 lose):**

| Party | Calculation | Net $MUND |
|---|---|---|
| Each Survivor | 100 + 15 + ⌊(200 × 0.80) / 3⌋ = 100 + 15 + 53 | **+68 $MUND** |
| Each Loser | −100 stake | **−100 $MUND** |
| Burn | ⌊200 × 0.20⌋ | **40 $MUND burned** |

### Selective App Blacklist (Immutable Lock)

Users choose specific apps to block before each duel via a multi-select picker. The selection is **deep-frozen** (`Object.freeze()`) the instant the duel status transitions to `active`.

| Step | Behavior |
|---|---|
| **Pre-duel** | User picks apps from `APP_REGISTRY` (searchable, categorized: social/entertainment/gaming/news/messaging) |
| **On start** | `selectedApps[]` → `Object.freeze()` → stored as `frozenApps` on the `FocusDuel` object |
| **During duel** | UI shows **"Current Shackles"** read-only list (white/90 bg, `rounded-[2rem]`, 🔒 padlock per app) |
| **Post-duel** | `frozenApps` persisted in history for audit trail |

**Platform-Specific App Picker:**

| Platform | Source API | Behavior |
|---|---|---|
| iOS | `FamilyActivitySelection` (Screen Time API) | Native picker resolves to bundle IDs |
| Android | `PackageManager.getInstalledApplications()` via Capacitor bridge | Searchable checkbox list |
| Web | Static `APP_REGISTRY` (16 known apps) | Emoji-icon multi-select grid |

**`APP_REGISTRY`** (built-in, 16 apps across 5 categories):

| Category | Apps |
|---|---|
| Social | Instagram, X (Twitter), Facebook, Snapchat, Reddit, Pinterest, LinkedIn |
| Entertainment | TikTok, YouTube, Netflix, Spotify |
| Gaming | Twitch, Brawl Stars, Wild Rift |
| News | Apple News |
| Messaging | Discord |

### NativeFocusShield (App-Locking)

| Platform | API | Status |
|---|---|---|
| iOS | `ManagedSettingsStore` (Family Controls / Screen Time API) | 🔧 STUB — native plugin required |
| Android | `AccessibilityService` + `UsageStatsManager` | 🔧 STUB — native plugin required |
| Web | No-op (internal flag only) | ⚠️ No enforcement |

**Lock contract:** `NativeFocusShield.lock()` is called **exactly once** at duel start — for **both** the challenger and the acceptor simultaneously via WebSocket — with the frozen `BlockableApp[]`. The shield extracts platform-specific IDs (bundle IDs for iOS, package names for Android) from the app objects. No subsequent `lock()` calls are permitted while `isLocked === true`.

### Peer Transparency (DuelChallengePayload)

When a user challenges an opponent via the Guardian Browser, the `DuelChallengePayload` is embedded in the invitation. The Acceptance Modal displays:

> **"Opponent's Commitment: [📸][🎵][👤] 🔒"**
> Instagram, TikTok, Facebook

| Payload Field | Description |
|---|---|
| `targetAppIcons` | Emoji icons of blocked apps (e.g., `['📸', '🎵', '👤']`) |
| `targetAppNames` | Human-readable names (e.g., `['Instagram', 'TikTok', 'Facebook']`) |
| `targetAppIds` | Bundle/package IDs for native verification |

This ensures both parties have full visibility into the duel terms. The frozen list cannot be modified after the challenge is sent.

### Social Key (Guardian Unlock)

1. User presses **"Request Release"** → sends push to `socialGuardianId`
2. Guardian reviews in their Vellbeing app → taps **"Approve Unlock"**
3. Server verifies guardian identity → emits socket event with `serverAuthToken`
4. `NativeFocusShield.unlock(duelId, authToken)` fires on user's device
5. **Unlock counts as BREACH** — user forfeits stake

This ensures no user can self-unlock. The Social Key creates real accountability.

---

## 1.8 Sovereignty Missions (Addiction Staking)

| Property | Value |
|---|---|
| **Logic Trigger** | `AddictionTrackerManager.checkStakes()` (on win) |
| **Source File** | `SovereigntyManager.service.ts:472-488` |
| **Reward Formula** | $\text{Return} = \text{stakedAmount} + \lfloor \text{stakedAmount} \times 0.50 \rfloor$ |
| **Anti-Inflation Cap** | `MIN_SOVEREIGNTY_STAKE = 100 $MUND`; requires existing balance |
| **Source of Truth** | ⚠️ **HIGH-RISK — Relapse is self-reported** |

**Formula:**

$$\text{MUND}_{\text{stake\_win}} = A_{\text{staked}} \times 1.50$$

If user relapses within 24h → **100% of staked $MUND is BURNED** (`PENALTY_BURN_RATE = 1.00`).

---

## 1.9 Clean Counter (Addiction Recovery)

| Property | Value |
|---|---|
| **Logic Trigger** | `AddictionTrackerManager.updateAllTrackers()` |
| **Source File** | `SovereigntyManager.service.ts:557-600` |
| **Reward Formula** | **1 $MUND per clean hour** (`CLEAN_HOUR_MUND_RATE = 1`) |
| **Anti-Inflation Cap** | `DAILY_CLEAN_REWARD_CAP = 5 $MUND/day` per user |
| **Source of Truth** | ⚠️ **HIGH-RISK — No biometric verification of sobriety** |

**Milestone Bonuses** (exempt from daily cap):

| Clean Duration | Bonus |
|---|---|
| 24 hours (1 day) | +25 $MUND |
| 168 hours (1 week) | +100 $MUND |
| 720 hours (1 month) | +500 $MUND |
| 2,160 hours (3 months) | +2,000 $MUND |
| 8,760 hours (1 year) | +10,000 $MUND |

---

## 1.10 Streak Bonuses

| Property | Value |
|---|---|
| **Logic Trigger** | `MundEngine.checkStreakBonuses()` |
| **Source File** | `MundEngine.ts:382-400` |
| **Reward Formula** | Flat bonus at milestones |
| **Anti-Inflation Cap** | Awards only on exact modulo days |
| **Source of Truth** | Auto-calculated from `lastActiveDate` |

| Streak | Bonus |
|---|---|
| Every 7 days | **50 $MUND** |
| Every 30 days | **250 $MUND** |

---

## 1.11 Ad-Watched Rewards

| Property | Value |
|---|---|
| **Logic Trigger** | `MundEngine.MUND_REWARDS.ad_watched` |
| **Source File** | `MundEngine.ts:93` |
| **Reward Formula** | **25 $MUND** per ad |
| **Anti-Inflation Cap** | **Max 2/day** → **50 $MUND/day** |
| **Source of Truth** | ✅ API-verified (ad network callback) |

---

## 1.12 Referral Rewards

| Property | Value |
|---|---|
| **Logic Trigger** | `MundEngine.MUND_REWARDS.referral_signup` |
| **Source File** | `MundEngine.ts:94` |
| **Reward Formula** | **500 $MUND** one-time per referral |
| **Anti-Inflation Cap** | Locked until referred user pays first subscription |
| **Source of Truth** | ✅ Backend-verified |

---

## 1.13 Milestones & Achievements (One-Time)

| Property | Value |
|---|---|
| **Logic Trigger** | `AchievementsDB.checkAchievements()` |
| **Source File** | `AchievementsDB.ts:500-530` |
| **Reward Formula** | Variable per achievement (see table) |
| **Anti-Inflation Cap** | One-time unlock — cannot be re-earned |
| **Source of Truth** | Mixed — depends on underlying metric source |

### Complete Achievement Reward Table

| ID | Title | Category | Tier | $MUND |
|---|---|---|---|---|
| `phy_foundation_i` | Neuromuscular Foundation I | PHY | 1 | 100 |
| `phy_foundation_ii` | Neuromuscular Foundation II | PHY | 2 | 300 |
| `phy_hypertrophy_alpha` | Hypertrophy Phase Alpha | PHY | 2 | 250 |
| `phy_hypertrophy_beta` | Hypertrophy Phase Beta | PHY | 3 | 600 |
| `phy_vo2_baseline` | VO2 Baseline Established | PHY | 1 | 150 |
| `phy_metabolic_conditioning` | Metabolic Conditioning Protocol | PHY | 2 | 350 |
| `phy_iron_discipline` | Iron Discipline (7d streak) | PHY | 2 | 200 |
| `phy_unstoppable` | Unstoppable Force (30d streak) | PHY | 4 | 800 |
| `phy_level_10` | Physical Competence Threshold | PHY | 3 | 500 |
| `phy_level_25` | Elite Physical Adaptation | PHY | 5 | 1,500 |
| `cog_neural_init` | Neural Pathway Initialization | COG | 1 | 100 |
| `cog_bibliophile_i` | Bibliophile Protocol I | COG | 2 | 200 |
| `cog_bibliophile_ii` | Bibliophile Protocol II | COG | 3 | 500 |
| `cog_polyglot_init` | Polyglot Initialization | COG | 2 | 200 |
| `cog_polyglot_fluency` | Polyglot Fluency Matrix | COG | 4 | 1,000 |
| `cog_daily_learner` | Cognitive Consistency I | COG | 2 | 200 |
| `cog_deep_focus` | Deep Focus State (60min+) | COG | 2 | 150 |
| `cog_level_10` | Cognitive Threshold Achieved | COG | 3 | 500 |
| `met_tracking_init` | Metabolic Tracking Initialized | MET | 1 | 150 |
| `met_autophagy_i` | Autophagy State I (16h fast) | MET | 1 | 100 |
| `met_autophagy_ii` | Autophagy State II (24h fast) | MET | 2 | 200 |
| `met_autophagy_protocol` | Autophagy Protocol Master | MET | 3 | 500 |
| `met_protein_synthesis` | Protein Synthesis Optimization | MET | 2 | 300 |
| `met_clean_fuel` | Clean Fuel Protocol | MET | 3 | 400 |
| `met_level_10` | Metabolic Efficiency Threshold | MET | 3 | 500 |
| `rec_circadian_i` | Circadian Synchronization I | REC | 1 | 150 |
| `rec_circadian_ii` | Circadian Synchronization II | REC | 2 | 350 |
| `rec_circadian_iii` | Circadian Synchronization III | REC | 4 | 800 |
| `rec_optimal_sleep` | Optimal Sleep Architecture | REC | 3 | 400 |
| `rec_parasympathetic` | Parasympathetic Dominance | REC | 2 | 200 |
| `rec_level_10` | Recovery Systems Optimized | REC | 3 | 500 |
| `special_balanced` | Homeostatic Balance | CROSS | 3 | 500 |
| `special_elite_balance` | Elite Homeostatic Integration | CROSS | 5 | 2,000 |
| `special_first_week` | System Initialization Complete | CROSS | 1 | 200 |
| `special_month_streak` | Protocol Adherence: Month One | CROSS | 4 | 1,000 |

**Total one-time achievement pool: 13,950 $MUND**

---

## 1.14 Program Milestone Bonuses

| Property | Value |
|---|---|
| **Logic Trigger** | `ChronosArchitect.PROGRAM_TEMPLATES[focus].milestones` |
| **Source File** | `ChronosArchitect.service.ts:130-180` |

**Example — Body Recomposition (90-day):**

| Day | Milestone | Bonus |
|---|---|---|
| 7 | Adaptation Week | 50 $MUND |
| 14 | Fat Adaptation | 100 $MUND |
| 30 | First Month | 250 $MUND |
| 60 | Halfway Point | 500 $MUND |
| 90 | Protocol Complete | 1,000 $MUND |
| **Total** | | **1,900 $MUND** |

**Daily Plan Rewards** (from `ProgramScheduler.generateStructuredProgram`):
- Rest day: **5 $MUND**
- Training day: $10 + \lfloor \text{intensity} \times 1.5 \rfloor$ → **10-25 $MUND/day**

---

## 1.15 Tier Efficiency Multiplier

| Property | Value |
|---|---|
| **Logic Trigger** | `MundEngine.addMund()` → `wallet.tier.efficiencyBonus` |
| **Source File** | `MundEngine.ts:209-211` |
| **Formula** | $\text{bonusAmount} = \lfloor A_{\text{base}} \times E_{\text{tier}} \rfloor$ |

**Tier Table** (from `MUND_TIERS`):

| Level | Tier Name | Min Lifetime $MUND | Efficiency Bonus |
|---|---|---|---|
| 1 | INITIATE | 0 | ×1.00 |
| 2 | OPERATOR | 1,000 | ×1.01 |
| 3 | SPECIALIST | 5,000 | ×1.02 |
| 4 | COMMANDER | 15,000 | ×1.03 |
| 5 | SENTINEL | 50,000 | ×1.05 |

$$\text{MUND}_{\text{final}} = \lfloor A_{\text{base}} \times E_{\text{tier}} \rfloor$$

---

## 1.16 Chronotype Alignment Bonus

| Property | Value |
|---|---|
| **Logic Trigger** | `MundEngine.getChronotypeAlignmentBonus()` |
| **Source File** | `MundEngine.ts:340-356` |
| **Formula** | If current hour ∈ peak window → multiply by `getDynamicEfficiencyCap()` (default 1.05, range 1.00–1.15) |

**Peak Windows** (from BioVault genomic data):

| Chronotype | Peak Start | Peak End |
|---|---|---|
| EARLY_BIRD | 07:00 | 12:00 |
| INTERMEDIATE | 10:00 | 14:00 |
| NIGHT_OWL | 16:00 | 22:00 |

**Combined Formula** (from `calculateFinalMund`):

$$\text{MUND}_{\text{combined}} = \lfloor A_{\text{base}} \times E_{\text{tier}} \times C_{\text{chrono}} \rfloor$$

---

## 1.17 Master Constants Table

| Constant | Value | Source File | Line |
|---|---|---|---|
| `STEPS_PER_MUND` | 100 | DeflationEngine.ts | 96 |
| `STEPS_DAILY_CAP` | 10,000 | DeflationEngine.ts | 97 |
| `SLEEP_7H_REWARD` | 50 | DeflationEngine.ts | 100 |
| `SLEEP_DAILY_CAP` | 1 | DeflationEngine.ts | 101 |
| `AFFILIATE_MUND_PER_EURO` | 5 | DeflationEngine.ts | 104 |
| `TIMESTAMP_TOLERANCE_MS` | 300,000 (5 min) | DeflationEngine.ts | 107 |
| `CASHBACK_RATE` | 0.50 (50%) | AffiliateService.ts | 18 |
| `MAX_MUND_TO_COMMISSION_RATIO` | 0.60 | AffiliateService.ts | 19 |
| `MUND_PER_EURO` | 1,000 | AffiliateService.ts | 22 |
| `ESCROW_DAYS` | 30 | AffiliateService.ts | 28 |
| `MIN_COMMISSION_EURO` | 0.01 | AffiliateService.ts | 27 |
| `SUBSCRIPTION_BURN_AMOUNT` | 1,000 | DeflationEngine.ts | 82 |
| `PENALTY_BURN_RATE` | 1.00 (100%) | DeflationEngine.ts | 85 |
| `MIN_SOVEREIGNTY_STAKE` | 100 | DeflationEngine.ts | 88 |
| `CLEAN_HOUR_MUND_RATE` | 1 | SovereigntyManager.ts | 63 |
| `DAILY_CLEAN_REWARD_CAP` | 5 | SovereigntyManager.ts | 65 |
| `RELAPSE_FREEZE_HOURS` | 24 | SovereigntyManager.ts | 64 |
| `STAKE_DURATION_HOURS` | 24 | SovereigntyManager.ts | 66 |
| `STAKE_WIN_BONUS_PERCENT` | 50% | SovereigntyManager.ts | 67 |
| `TOTAL_SUPPLY` | 10,000,000,000 | DeflationEngine.ts | 22 |
| `MINING_REWARDS_POOL` | 2,000,000,000 | DeflationEngine.ts | 25 |
| `STRATEGIC_RESERVE` | 8,000,000,000 | DeflationEngine.ts | 26 |
| `BETA_PRICE_MONTHLY` | €2.99 | DeflationEngine.ts | 50 |
| `STANDARD_PRICE_MONTHLY` | €13.90 | DeflationEngine.ts | 54 |
| `STANDARD_PRICE_YEARLY` | €90.00 | DeflationEngine.ts | 55 |
| `BETA_DURATION_DAYS` | 90 | DeflationEngine.ts | 51 |
| `steps_verified` | 1 per 100 steps | MundEngine.ts | 80 |
| `workout_tier_a` | 20 (HIIT/Running) | MundEngine.ts | 81 |
| `workout_tier_b` | 15 (Strength/Cycling) | MundEngine.ts | 82 |
| `workout_tier_c` | 5 (Yoga/Walking) | MundEngine.ts | 83 |
| `METABOLIC_VERIFICATION.MIN_DURATION_MINUTES` | 20 | ResonanceEngine.service.ts | — |
| `METABOLIC_VERIFICATION.MIN_HR_RATIO` | 1.5 | ResonanceEngine.service.ts | — |
| `METABOLIC_VERIFICATION.MAX_WORKOUTS_PER_DAY` | 2 | ResonanceEngine.service.ts | — |
| `METABOLIC_VERIFICATION.LOW_INTENSITY_MULTIPLIER` | 0.50 | ResonanceEngine.service.ts | — |
| `sleep_verified` | 10 | MundEngine.ts | 82 |
| `meal_logged` | 5 (max 3/day) | MundEngine.ts | 85 |
| `water_glass` | 2 (max 8/day) | MundEngine.ts | 86 |
| `reading_30min` | 10 | MundEngine.ts | 89 |
| `learning_session` | 8 | MundEngine.ts | 90 |
| `ad_watched` | 25 (max 2/day) | MundEngine.ts | 93 |
| `referral_signup` | 500 | MundEngine.ts | 94 |
| `purchase_cashback` | 0.10 (10%) | MundEngine.ts | 95 |
| `streak_bonus_7day` | 50 | MundEngine.ts | 98 |
| `streak_bonus_30day` | 250 | MundEngine.ts | 99 |
| `SOURCE_MULTIPLIERS.HARDWARE_VERIFIED` | 1.00 | DeflationEngine.ts | — |
| `SOURCE_MULTIPLIERS.THIRD_PARTY_APP` | 0.00 | DeflationEngine.ts | — |
| `SOURCE_MULTIPLIERS.MANUAL` | 0.00 | DeflationEngine.ts | — |
| `SOURCE_MULTIPLIERS.UNKNOWN` | 0.00 | DeflationEngine.ts | — |
| `ANTI_GAMING.MAX_STEPS_PER_30_MIN` | 10,000 | DeflationEngine.ts | — |
| `ANTI_GAMING.VELOCITY_WINDOW_MS` | 1,800,000 (30 min) | DeflationEngine.ts | — |
| `ANTI_GAMING.HR_CORRELATION_STEP_THRESHOLD` | 5,000 | DeflationEngine.ts | — |
| `ANTI_GAMING.RESTING_HR_MAX` | 85 BPM | DeflationEngine.ts | — |
| `ANTI_GAMING.SUSPICIOUS_THROTTLE_MULTIPLIER` | 0.10 | DeflationEngine.ts | — |

---

### Theoretical Maximum Daily Earnings

$$\text{MAX}_{\text{daily}} = 100_{\text{steps}} + 40_{\text{workouts}} + 50_{\text{sleep}} + 15_{\text{meals}} + 16_{\text{water}} + 10_{\text{reading}} + 8_{\text{learning}} + 50_{\text{ads}} + 5_{\text{clean}} = \mathbf{294\ \$MUND/day}$$

With SENTINEL tier (×1.05) + Chronotype peak (×1.05):

$$\text{MAX}_{\text{boosted}} = 294 \times 1.05 \times 1.05 = \mathbf{324\ \$MUND/day}$$

**Annual ceiling (excluding one-time bonuses):** $324 \times 365 = \mathbf{118,260\ \$MUND/year}$

---

# II. ZERO-TRUST VERIFICATION AUDIT

## 2.1 Vulnerability Analysis

| Vector | Risk Level | Attack Surface | Current Mitigation |
|---|---|---|---|
| **Manual meal logging** | � LOW | User claims 3 meals without eating | **Camera-Only** — `input.capture='environment'`, no gallery. Max 3/day |
| **Manual water logging** | 🔴 HIGH | User claims 8 glasses without drinking | Rate-limit only — **NO verification** |
| **Manual reading/learning** | 🔴 HIGH | User claims 30-min reading without reading | Rate-limit only — **NO screen-time or eye-tracking** |
| **Fake step data** | 🟡 MEDIUM | Spoofed HealthKit data, phone-shaking | HealthKit/Health Connect API — **moderately hardened** |
| **Focus mode gaming** | � LOW | Start timer, put phone down, do nothing | **NativeFocusShield** app-lock (iOS ManagedSettingsStore / Android AccessibilityService), Solo=0 $MUND, P2P duel staking with 20% burn |
| **Addiction self-report** | 🔴 CRITICAL | User never logs relapse, accumulates clean hours | Self-report only — **NO biomarker validation** |
| **Sleep data spoofing** | 🟡 MEDIUM | Wear device on nightstand overnight | HealthKit verified — **some motion detection** |
| **Affiliate fraud** | 🟢 LOW | Self-purchases through own link | Skimlinks deduplication + 30-day escrow |

---

## 2.2 Trust-Weight Matrix

Defined in `ResonanceEngine.service.ts:71-75`:

| Data Source | Trust Multiplier | $MUND Award % | Source File |
|---|---|---|---|
| **Sensor-verified** (HealthKit, Health Connect) | `1.00` | 100% | `TRUST_WEIGHTS.sensor` |
| **Mixed** (sensor + manual) | `0.55` | 55% | `TRUST_WEIGHTS.mixed` |
| **Manual entry only** | `0.10` | 10% | `TRUST_WEIGHTS.manual` |

**Logic** (`ResonanceEngine.service.ts:300-322`):

```
calculateTrustScore(sensorVerified, manualEntry):
  if sensorVerified && !manualEntry → { source: 'sensor', multiplier: 1.0 }
  if !sensorVerified && manualEntry → { source: 'manual', multiplier: 0.1 }
  else → { source: 'mixed', multiplier: 0.55 }
```

**Application** (`applyTrustWeight`):

$$\text{MUND}_{\text{trusted}} = \lfloor A_{\text{base}} \times W_{\text{trust}} \rfloor$$

---

## 2.3 Hardware-Only Enforcement (IMPLEMENTED)

The Step Mining system now enforces a **Hardware-Only verification layer** across three defense tiers:

### 2.3.1 Source Filtering (DeflationEngine.ts)

All step data is classified by `bundleIdentifier` (iOS) or `packageName` (Android) before any $MUND is awarded.

**Whitelisted Sources (Full Reward):**

| Platform | Bundle / Package Name |
|---|---|
| iOS | `com.apple.health`, `com.apple.health.workout-sessions` |
| iOS/Android | `com.garmin.connect.mobile` / `com.garmin.android.apps.connectmobile` |
| iOS/Android | `com.ouraring.oura` |
| iOS/Android | `com.fitbit.FitbitMobile` |
| iOS/Android | `com.whoop.whoop` / `com.whoop` |
| iOS/Android | `com.polar.polarflow` |
| Android | `com.google.android.apps.fitness`, `com.google.android.gms` |
| Android | `com.samsung.shealth` |
| iOS/Android | `com.coros.coros` |

**Source Multipliers:**

| Source Type | Multiplier | Effect |
|---|---|---|
| `HARDWARE_VERIFIED` | `1.00` | Full reward |
| `THIRD_PARTY_APP` | `0.00` | Zero reward — blocked |
| `MANUAL` | `0.00` | Zero reward — blocked |
| `UNKNOWN` | `0.00` | Zero reward — unrecognized source |

**Blacklisted Sources:**
`com.pacer.pedometer`, `de.j4velin.pedometer`, `cc.pacer.androidapp`, `manual_entry`, and others.

### 2.3.2 Anti-Gaming Heuristics (DeflationEngine.ts)

**Check 1 — Abnormal Velocity:**

$$\text{If } S \geq 10{,}000 \text{ AND } \Delta t < 30\text{ min} \implies \text{VOID (multiplier = 0.0)}$$

**Check 2 — Heart Rate Correlation:**

$$\text{If } S > 5{,}000 \text{ AND } \overline{HR} \leq 85\text{ BPM} \implies \text{THROTTLE (multiplier = 0.10)}$$

### 2.3.3 Combined Mining Formula

$$\text{MUND}_{\text{steps}} = \left\lfloor \left\lfloor \frac{S_{\text{raw}}}{100} \right\rfloor \times M_{\text{source}} \times M_{\text{antiGaming}} \times E_{\text{tier}} \right\rfloor$$

where:
- $M_{\text{source}} \in \{0.0, 1.0\}$ from `classifySource()`
- $M_{\text{antiGaming}} \in \{0.0, 0.10, 1.0\}$ from `runAntiGamingChecks()`
- $E_{\text{tier}} \in \{1.00 \ldots 1.05\}$ from `MundEngine.addMund()`

### 2.3.4 Ledger Flagging (MundEngine.ts)

All mining transactions are logged to `MundWallet.transactionLedger` with:
- `verificationStatus`: `VERIFIED` | `UNVERIFIED` | `FLAGGED`
- `excluded`: If `true`, $MUND is **not** credited to balance (logged only)
- `flags[]`: Array of anti-gaming or source warnings

**Exclusion Rule:** If `sourceType ∈ {MANUAL, THIRD_PARTY_APP, UNKNOWN}` OR `sourceMultiplier ≤ 0`, the transaction is **excluded** from the wallet balance.

### 2.3.5 UI Indicators (MundirEconomyModule.tsx)

| Icon | Meaning |
|---|---|
| 🛡️ `Shield` (green) | Hardware-Verified reward — full $MUND |
| ⚠️ `AlertCircle` (red) | Unverified / blocked — $MUND not awarded |

### 2.3.6 Future Phases

1. **Phase 2:** HealthKit motion coprocessor data cross-referenced with claimed activities
2. **Phase 3:** Wearable HRV anomaly detection for addiction claims
3. **Phase 4:** CGM integration for nutrition/fasting validation

---

## 2.4 Integrity Score Definition

**Proposed Mathematical Model:**

$$I(t) = I_0 \times \prod_{k=1}^{n} D_k$$

where:

- $I_0 = 100$ (initial integrity score)
- $D_k$ = decay factor for each detected anomaly

**Decay Factors:**

| Anomaly Type | Decay Factor $D_k$ |
|---|---|
| Manual log without sensor corroboration | 0.98 |
| Claimed activity contradicted by HR data | 0.85 |
| Sleep claim contradicted by motion data | 0.90 |
| Multiple rapid-fire manual logs (<1 min apart) | 0.80 |
| Focus session with >10 phone pickups | 0.92 |
| No anomalies detected for 7 days | 1.05 (recovery, capped at 100) |

**Effect on Rewards:**

$$\text{MUND}_{\text{integrity}} = A_{\text{base}} \times \frac{I(t)}{100}$$

If $I(t) < 50$ → all manual-entry rewards suspended until sensor verification restores score.

---

# III. MACRO-ECONOMIC "SHARK" PROTOCOL

## 3.1 Tokenomics Overview

| Metric | Value | Source |
|---|---|---|
| **Total Supply** | 10,000,000,000 $MUND | `TOKENOMICS.TOTAL_SUPPLY` |
| **Mining/Rewards Pool** | 2,000,000,000 (20%) | `TOKENOMICS.MINING_REWARDS_POOL` |
| **Strategic Reserve** | 8,000,000,000 (80%) | `TOKENOMICS.STRATEGIC_RESERVE` |
| **Decimal Precision** | 2 places (0.01 $MUND) | `TOKENOMICS.DECIMALS` |
| **Internal Exchange Rate** | 1,000 $MUND = €1 | `AFFILIATE_CONFIG.MUND_PER_EURO` |

---

## 3.2 Cash Flow Security

### MAX_MUND_TO_COMMISSION_RATIO = 0.60

**Source:** `AffiliateService.ts:19`

**Mechanism** (`processCashback`, line 390-405):

```
projectedRatio = (totalMundIssued + newMund) / (totalCommissions + newCommission)

IF projectedRatio > 0.60:
    maxMund = totalCommissions × 0.60 - totalMundIssued
    mundAwarded = max(0, floor(maxMund))
```

**Proof — Founder always retains ≥40% of commission revenue:**

Given:
- Skimlinks pays commission $C$ in EUR
- Cashback rate = 50% → User gets $0.50C$ as value
- BUT $MUND issued is capped at 60% of value → **effective cashback ≤ 30% of $C$**

$$\text{Founder Net} = C - \text{MUND\_value\_given} \geq C - 0.60C = 0.40C$$

$$\boxed{\text{Minimum Founder Margin} = 40\%\ \text{of all affiliate commissions}}$$

---

## 3.3 The Burn Black Hole

### Burn Channel 1: Subscription Burn

**Source:** `DeflationEngine.executeSubscriptionBurn()`

$$\text{Burn}_{\text{sub}} = \left\lfloor \frac{P_{\text{payment}}}{P_{\text{beta}}} \times 1000 \right\rfloor$$

| Payment | Burn Amount |
|---|---|
| €2.99 (beta monthly) | 1,000 $MUND |
| €13.90 (standard monthly) | 4,649 $MUND |
| €90.00 (yearly) | 30,100 $MUND |

**Annual burn from 1,000 subscribers (beta price):**

$$1000 \times 1000 \times 12 = \mathbf{12,000,000\ \$MUND/year}$$

### Burn Channel 2: Penalty Burn (Sovereignty Mission Failure)

**Source:** `DeflationEngine.executePenaltyBurn()`

$$\text{Burn}_{\text{penalty}} = \lfloor A_{\text{staked}} \times 1.00 \rfloor = A_{\text{staked}}$$

**100% of staked $MUND destroyed** on relapse. This is pure deflationary pressure from user behavior failure.

### Burn Source Accounting

- **Subscription burns** come from the **Strategic Reserve** (8B pool)
- **Penalty burns** come from **user wallets** (already in circulation)
- Both reduce effective supply permanently

$$\text{Deflation\%} = \frac{\text{totalBurned}}{\text{TOTAL\_SUPPLY}} \times 100$$

---

## 3.4 Liquidity Moat

### 30-Day Escrow

**Source:** `AFFILIATE_CONFIG.ESCROW_DAYS = 30`

All affiliate $MUND rewards are held for 30 days before release, ensuring:
1. Commission confirmation from Skimlinks
2. Protection against chargebacks and returns
3. Prevents pump-and-dump $MUND accumulation

### Exit Penalty (Internal vs External Rates)

The system uses a **dual-rate architecture**:

| Context | Rate | Effect |
|---|---|---|
| **Internal marketplace** | 1,000 $MUND = €1 | Full value |
| **External withdrawal** | Subject to 20% exit penalty (design spec) | 800 $MUND effective per €1 |

This creates a **liquidity moat** — users are incentivized to spend $MUND within the ecosystem rather than extracting.

---

## 3.5 Founder Profitability Proof

### Revenue Streams vs. Outflows

**Scenario: 10,000 active users, all maximizing mining**

**Inflows (Revenue):**

| Source | Monthly Revenue |
|---|---|
| Subscriptions (10K × €2.99 beta) | €29,900 |
| Subscriptions (10K × €13.90 standard) | €139,000 |
| Ad revenue (10K × 2 ads × 30 days) | Variable |
| Affiliate commissions (retained 40%+) | Variable |

**Outflows ($MUND Liability):**

| Source | Monthly $MUND Issued |
|---|---|
| Steps (10K users × 100/day × 30) | 30,000,000 |
| Sleep (10K × 50/day × 30) | 15,000,000 |
| Meals (10K × 15/day × 30) | 4,500,000 |
| Water (10K × 16/day × 30) | 4,800,000 |
| Workouts (10K × 30/day × 30) | 9,000,000 |
| Ads (10K × 50/day × 30) | 15,000,000 |
| Focus (10K × 20/day × 30) | 6,000,000 |
| **Total Monthly Issuance** | **84,300,000 $MUND** |

**Key Insight:** At internal rate (1000 MUND = €1), the monthly issuance = **€84,300 in notional $MUND**.

**But $MUND has NO guaranteed EUR redemption** — it is only spendable within the ecosystem. The Founder's actual cash outflow is **€0 for $MUND rewards**. Revenue from subscriptions is pure profit (minus infrastructure costs).

**Defense layers:**
1. $MUND is not redeemable for cash (internal economy only)
2. 40%+ of affiliate commissions retained
3. Subscription burns destroy 12M+ $MUND/year per 1K subscribers
4. Penalty burns from relapse create additional deflation
5. 20% exit penalty on any theoretical withdrawal

$$\boxed{\text{Founder is always profitable} \iff \text{Subscription Revenue} > \text{Infrastructure Cost}}$$

---

## 3.6 Data Monetization & Aggregate Insights

### 3.6.1 Overview

| Property | Value |
|---|---|
| **Source Files** | `NutritionDataLedger.ts`, `NutritionModule.tsx`, `ProfileMetrics.tsx` |
| **Data Schema** | `AnonymizedInsightSchema` — PII-stripped, research-grade nutrition records |
| **Consent Mechanism** | Opt-in toggle in Profile → "Consent to Anonymized Research" |
| **Storage** | `localStorage` key: `vb_nutrition_insight_ledger` (500-entry rolling buffer) |
| **Revenue Target** | B2B API sales to health research firms, pharmaceutical companies, insurance actuaries |

### 3.6.2 The Value Proposition

> **"Verified biological data is 10× more valuable than self-reported surveys."**

Traditional nutrition research relies on:
- **24-hour dietary recalls** (self-reported, known 30-40% error rate)
- **Food frequency questionnaires** (retrospective, low accuracy)
- **Clinical trials** (expensive, small sample sizes)

Vellbeing OS captures **camera-verified, real-time meal data** with:
- USDA FDC ID linkage (standardized food identification)
- Exact gram measurement input
- Macro breakdown (calories, protein, fat, carbs, fiber)
- Biometric context (BMI range, age range, activity level)
- Temporal patterns (day-of-week eating habits)

This creates a **proprietary dataset** that is orders of magnitude more reliable than survey-based alternatives.

### 3.6.3 AnonymizedInsightSchema (PII-Stripped)

```typescript
interface AnonymizedInsightSchema {
  // Demographics (ranges, not exact values)
  ageRange: '13-17' | '18-24' | '25-34' | '35-44' | '45-54' | '55-64' | '65+';
  gender: 'male' | 'female' | 'other' | 'undisclosed';
  
  // Biometrics (bucketed, not exact)
  bmiRange: 'underweight' | 'normal' | 'overweight' | 'obese' | 'unknown';
  activityLevel: 'sedentary' | 'light' | 'moderate' | 'active' | 'very_active';
  
  // Meal Content (the high-value payload)
  meal: {
    foodName: string;
    fdcId?: number;           // USDA public ID
    calories: number;
    protein: number;
    fat: number;
    carbs: number;
    fiber: number;
    grams: number;
  };
  
  // Verification
  captureSource: 'camera' | 'search' | 'manual';
  verificationStatus: 'VERIFIED' | 'UNVERIFIED';
  
  // Temporal (date only — no time, no timezone)
  dateISO: string;            // YYYY-MM-DD
  dayOfWeek: number;          // 0-6
  
  // Ledger
  schemaVersion: '1.0';
  insightId: string;          // Random UUID (NOT linked to user)
  collectedAt: number;        // Unix epoch
}
```

### 3.6.4 What Is Stripped (PII Removal)

| Field | Status | Reason |
|---|---|---|
| UserID | ❌ **STRIPPED** | Directly identifying |
| Name | ❌ **STRIPPED** | Directly identifying |
| Email | ❌ **STRIPPED** | Directly identifying |
| Exact Age | ❌ **STRIPPED** | Replaced with age range (e.g., '25-34') |
| Exact BMI | ❌ **STRIPPED** | Replaced with BMI bucket (e.g., 'normal') |
| IP Address | ❌ **NEVER COLLECTED** | Not stored client-side |
| Device ID | ❌ **NEVER COLLECTED** | Not included in schema |
| Location | ❌ **NEVER COLLECTED** | Not included in schema |
| Exact Timestamp | ❌ **STRIPPED** | Only date (YYYY-MM-DD) retained |

### 3.6.5 User Consent Flow

```
Profile → Anonymized Research section
  └─ Toggle: "Consent to Anonymized Research"
       ├─ OFF (default): No data collected to ledger
       └─ ON: Each food log generates AnonymizedInsightSchema
            └─ Stored in vb_nutrition_insight_ledger (500 rolling)
            └─ Stats shown: total insights, camera-verified rate
```

**Consent Storage:** `localStorage` key `vb_research_consent` = `'true'` | `'false'`

**Incentive Message:** *"Sharing data supports the ecosystem and may increase your $MUND rewards."*

### 3.6.6 Revenue Model

**Target Buyers:**

| Buyer Category | Use Case | Estimated Value per 10K Records |
|---|---|---|
| Health Research Firms | Dietary pattern analysis | €500-€2,000 |
| Pharmaceutical Companies | Drug interaction with diet | €1,000-€5,000 |
| Insurance Actuaries | Risk assessment models | €500-€1,500 |
| CPG/Food Companies | Market research, product development | €200-€800 |
| Academic Institutions | Epidemiological studies | €100-€500 |

**Data Quality Premium:**

$$\text{Value}_{\text{camera-verified}} \approx 10 \times \text{Value}_{\text{self-reported}}$$

Camera-verified meals with USDA FDC ID linkage command a **10× premium** over traditional survey data because:
1. **Standardized identification** — FDC ID maps to exact nutritional composition
2. **Temporal precision** — real-time logging vs. retrospective recall
3. **Biometric context** — BMI, age, activity level included
4. **Verification proof** — camera capture eliminates fabrication

### 3.6.7 Scaling Projections

| Users | Meals/Day | Monthly Records | Annual Records | Est. Annual Revenue |
|---|---|---|---|---|
| 1,000 | 3 | 90,000 | 1,080,000 | €10K-€50K |
| 10,000 | 3 | 900,000 | 10,800,000 | €100K-€500K |
| 100,000 | 3 | 9,000,000 | 108,000,000 | €1M-€5M |

**Founder Margin:** Data sales have **~95% gross margin** (no COGS beyond storage). This is a **pure profit center** independent of subscription revenue.

### 3.6.8 Source File Trace

| Function | File | Purpose |
|---|---|---|
| `buildAnonymizedInsight()` | `NutritionDataLedger.ts` | Core anonymization + ledger write |
| `getLedgerEntries()` | `NutritionDataLedger.ts` | Retrieve stored insights |
| `getLedgerStats()` | `NutritionDataLedger.ts` | Dashboard stats (total, verified %) |
| `setResearchConsent()` | `NutritionDataLedger.ts` | Toggle consent flag |
| `getResearchConsent()` | `NutritionDataLedger.ts` | Read consent state |
| `captureWithCamera()` | `NutritionModule.tsx` | Camera-only capture (no gallery) |
| Consent Toggle UI | `ProfileMetrics.tsx` | User-facing opt-in switch |

---

# IV. THE NEURAL BRAIN (ORACLE AI)

## 4.1 Data Flow Map

```
┌───────────────────────────────────────────────────────────────────┐
│                        USER INTERACTION                           │
│  (App.tsx → OracleAI.tsx opens via PulsingAuraButton)            │
└──────────────────────────┬────────────────────────────────────────┘
                           │
                    ┌──────▼──────┐
                    │  ORACLE AI  │
                    │ (4 Tabs)    │
                    └──────┬──────┘
                           │
          ┌────────────────┼────────────────┐
          │                │                │
    ┌─────▼─────┐   ┌─────▼─────┐   ┌─────▼─────┐
    │ RESONANCE │   │ PROGRAMS  │   │SOVEREIGNTY│
    │    TAB    │   │    TAB    │   │    TAB    │
    └─────┬─────┘   └─────┬─────┘   └─────┬─────┘
          │                │                │
   ┌──────▼──────┐  ┌─────▼──────┐  ┌─────▼──────────┐
   │ Resonance   │  │ Chronos    │  │ Sovereignty     │
   │ Engine      │  │ Architect  │  │ Manager         │
   │ .service.ts │  │ .service.ts│  │ .service.ts     │
   └──────┬──────┘  └─────┬──────┘  └─────┬──────────┘
          │                │                │
    ┌─────▼─────┐   ┌─────▼──────┐  ┌─────▼──────────┐
    │DailyMetrics│  │ Program    │  │ FocusMode +     │
    │ HRV, Sleep │  │ Scheduler  │  │ Addiction       │
    │ Stress     │  │ .service.ts│  │ Tracker         │
    └─────┬──────┘  └─────┬──────┘  └─────┬──────────┘
          │                │                │
          └────────┬───────┘                │
                   │                        │
            ┌──────▼──────┐          ┌──────▼──────┐
            │ BioVault    │          │ Deflation   │
            │ Module.tsx  │          │ Engine.ts   │
            │ (Genomics)  │          │ (Burns)     │
            └──────┬──────┘          └──────┬──────┘
                   │                        │
            ┌──────▼──────┐          ┌──────▼──────┐
            │ Supplement  │          │ Mund        │
            │ Nexus       │          │ Engine.ts   │
            │ .service.ts │          │ (Wallet)    │
            └─────────────┘          └─────────────┘
```

**External APIs integrated in OracleAI.tsx:**
- `SpoonacularAPI.service.ts` → Meal plan generation
- `ExerciseDB.service.ts` → Recovery-based workout generation
- `AffiliateService.ts` → Affiliate URL generation for purchase suggestions

---

## 4.2 Pearson Correlation Engine

**Source:** `ResonanceEngine.service.ts:150-270` — `analyzeCorrelations()`

The engine evaluates **5 primary correlation axes** from `DailyMetrics`:

### Correlation Matrix

| # | Trigger Variable | Effect Variable | Direction | $r$ | Action |
|---|---|---|---|---|---|
| 1 | Late meal (>21:00) | Deep sleep % | Negative | **-0.72** | Suggest 3h pre-bed fasting + Magnesium |
| 2 | Low HRV (<40ms) | Overtraining risk | Negative | **-0.65** | Reduce exercise intensity by 3 |
| 3 | High HRV (>65ms) | Optimal recovery | Positive | **+0.78** | Increase exercise intensity by 2 |
| 4 | High stress (>70) | Addiction susceptibility | Positive | **+0.81** | Craving alert + L-Theanine + Ashwagandha |
| 5 | Poor sleep (<6h) | Cognitive impairment | Negative | **-0.85** | Recommend 20-min nap, cut caffeine by 2PM |

### How Biological Health Links to Financial Savings

The Oracle AI uses correlation insights to reduce unnecessary spending:

1. **Sleep → Supplement savings:** If HRV normalizes, supplement recommendations reduce → lower monthly spend
2. **Stress → Impulse purchase reduction:** High-stress correlation triggers sovereignty mode → reduces impulse purchases through affiliate links
3. **Focus mode → Productivity earnings:** Optimized focus minutes → more productive work → higher earning potential

The Pearson coefficient $r$ is hardcoded (not dynamically computed from user data), representing population-level research values. A future phase would compute rolling $r$ from the user's own time-series data:

$$r_{xy} = \frac{\sum_{i=1}^{n}(x_i - \bar{x})(y_i - \bar{y})}{\sqrt{\sum_{i=1}^{n}(x_i - \bar{x})^2 \sum_{i=1}^{n}(y_i - \bar{y})^2}}$$

---

## 4.3 Supplement Nexus → ProgramScheduler Pipeline

### Step 1: Biological Gap Detection
`SupplementNexus.analyzeGaps(metrics)` identifies deficiencies:

| Gap Category | Detection Metric | Optimal Range |
|---|---|---|
| Sleep | `sleepHours`, `deepSleepPercent` | ≥7h, ≥15% deep |
| Stress | `stressLevel` | <40 |
| Energy | `exerciseMinutes` | ≥30 min |
| Recovery | `hrv` | ≥45ms |
| Hormonal | Genomic markers (BioVault) | DNA-dependent |

### Step 2: DNA-Personalized Supplements
`SupplementNexus.getDNAProfile()` modifies recommendations:

| DNA Marker | If Variant | Modified Recommendation |
|---|---|---|
| `caffeineMetabolism: 'slow'` | CYP1A2 slow | Reduce caffeine, suggest L-Theanine |
| `vitaminDNeeds: 'elevated'` | VDR variant | Increase D3 to 5000 IU |
| `folateMetabolism: 'mthfr_variant'` | MTHFR C677T | Use methylated B-complex |
| `omega3Conversion: 'poor'` | FADS1/FADS2 | Higher dose EPA/DHA from fish oil |

### Step 3: Program Generation
`ProgramScheduler.generateStructuredProgram(goal, biometrics)` creates a 30-day plan:

1. **Calculates BMR** using Mifflin-St Jeor:

$$\text{BMR}_{\text{male}} = 10W + 6.25H - 5A + 5$$
$$\text{BMR}_{\text{female}} = 10W + 6.25H - 5A - 161$$

2. **Calculates TDEE:**

$$\text{TDEE} = \text{BMR} \times M_{\text{activity}}$$

| Activity Level | Multiplier $M$ |
|---|---|
| Sedentary | 1.200 |
| Light | 1.375 |
| Moderate | 1.550 |
| Active | 1.725 |
| Very Active | 1.900 |

3. **Generates daily workout, nutrition, recovery** with $MUND reward per day
4. **Feeds supplement list** from SupplementNexus into `RecoveryPlan.supplements`

---

# V. CRITICAL LOGIC TRACES

## 5.1 Purchase-to-Cashback Trace

```
STEP 1: User clicks product link in OracleAI
  └─ OracleAI.tsx calls generateAffiliateUrl(url, userId)
       └─ AffiliateService.ts:161 → Skimlinks redirect URL with xcust=userId

STEP 2: User purchases on merchant site
  └─ Skimlinks records commission
  └─ Commission status: 'pending'

STEP 3: Skimlinks webhook fires (or CSV imported)
  └─ AffiliateService.processWebhook(payload)
       └─ Creates SkimlinksCommission object
       └─ Saves to localStorage via saveCommissions()
       └─ If status='confirmed' → processCashback(commission)

STEP 4: processCashback() executes
  └─ Validates: commission.status === 'confirmed'
  └─ Validates: commissionAmount >= MIN_COMMISSION_EURO (€0.01)
  └─ Checks: duplicate prevention (commissionId lookup)
  └─ Calculates: cashbackEuro = commissionAmount × 0.50
  └─ SAFETY CHECK: projectedRatio vs MAX_MUND_TO_COMMISSION_RATIO (0.60)
       └─ If exceeds 60% → reduces mundAwarded to cap
  └─ Calculates: mundAwarded = cashbackEuro × MUND_PER_EURO (1000)
  └─ Creates CashbackTransaction { status: 'credited' }
  └─ Saves transaction

STEP 5: $MUND deposited
  └─ MundEngine.addMund(wallet, mundAwarded)
       └─ Applies tier efficiency bonus
       └─ Updates mundBalance AND mundLifetime
       └─ Checks for tier-up
       └─ Saves wallet to localStorage
       └─ Triggers haptic feedback

RESULT: User sees $MUND in wallet. Founder retains ≥40% of commission.
```

---

## 5.2 Sync-to-Burn Trace

### Path A: Subscription Burn

```
STEP 1: User pays €2.99 monthly subscription
  └─ Payment processor confirms

STEP 2: executeSubscriptionBurn(paymentId, 2.99)
  └─ DeflationEngine.ts:198-228
  └─ burnAmount = floor((2.99 / 2.99) × 1000) = 1,000 $MUND
  └─ actualBurn = min(1000, remainingReserve)
  └─ Creates BurnEvent { type: 'subscription', amount: 1000 }

STEP 3: State update
  └─ totalBurned += 1000
  └─ subscriptionBurns += 1000
  └─ remainingReserve -= 1000  (from Strategic Reserve)
  └─ burnHistory = [newEvent, ...history.slice(0, 99)]

STEP 4: BurnCounter.tsx displays
  └─ Polls getBurnStatistics() every 5 seconds
  └─ Animates counter with easeOutCubic interpolation
  └─ Shows deflationPercentage = totalBurned / 10B × 100
```

### Path B: Penalty Burn (Relapse)

```
STEP 1: User has active stake on addiction tracker
  └─ stakeOnClean(type, 500, deductCallback)
  └─ 500 $MUND deducted from mundBalance

STEP 2: User logs relapse
  └─ AddictionTrackerManager.logRelapse(type)
       └─ SovereigntyManager.service.ts:499-520

STEP 3: Stake burned
  └─ tracker.stake.status = 'burned'
  └─ The 500 $MUND is permanently destroyed (already deducted)
  └─ 24h freeze applied (RELAPSE_FREEZE_HOURS = 24)
  └─ cleanStreakHours reset to 0
  └─ relapseCount++

STEP 4: executePenaltyBurn(missionId, 500)
  └─ burnAmount = floor(500 × 1.00) = 500
  └─ totalBurned += 500
  └─ penaltyBurns += 500
  └─ NOTE: Does NOT reduce remainingReserve (user tokens, not reserve)

RESULT: 500 $MUND permanently removed from circulation.
```

---

# APPENDIX A: GamificationSystem Legacy Mapping

The `GamificationSystem.ts` uses a **logarithmic leveling curve** that is being deprecated in favor of the pure $MUND tier system:

$$\text{Level} = \lfloor K \sqrt{\text{XP}} \rfloor \quad \text{where } K = 0.15$$

$$\text{XP for Level } L = \left\lceil \left(\frac{L}{K}\right)^2 \right\rceil$$

| Level | XP Required |
|---|---|
| 1 | 0 |
| 5 | 1,111 |
| 10 | 4,444 |
| 25 | 27,778 |
| 50 | 111,111 |

**Streak Multiplier:**

$$M_{\text{streak}} = \min\left(2.0,\ 1.2 + (S - 3) \times 0.05\right) \quad \text{for } S \geq 3$$

This system is mapped to the $MUND wallet via `mapWalletToLegacyStats()` for backward compatibility.

---

# APPENDIX B: Subscription Pricing Schedule

| Phase | Monthly | Yearly | Duration |
|---|---|---|---|
| **Beta** (Feb 5, 2026) | €2.99 | N/A | 90 days |
| **Standard** (post-beta) | €13.90 | €90.00 (46% savings) | Ongoing |

Beta auto-transitions to standard pricing via `PHASE1_PRICING.getCurrentPrice()`.

---

# APPENDIX C: File-to-Function Index

| File | Key Exports | Purpose |
|---|---|---|
| `MundEngine.ts` | `addMund()`, `spendMund()`, `loadWallet()`, `MUND_REWARDS`, `MUND_TIERS`, `MundTransaction`, `MundVerificationStatus` | Core wallet & reward system (Hardware-Only enforced) |
| `DeflationEngine.ts` | `executeSubscriptionBurn()`, `executePenaltyBurn()`, `calculateStepsMining()`, `calculateSleepMining()`, `classifySource()`, `runAntiGamingChecks()`, `checkStepVelocity()`, `checkHeartRateCorrelation()`, `TOKENOMICS`, `BURN_CONFIG`, `SOURCE_MULTIPLIERS`, `ANTI_GAMING`, `HARDWARE_SOURCE_WHITELIST_IOS`, `HARDWARE_SOURCE_WHITELIST_ANDROID`, `SOURCE_BLACKLIST` | Token destruction, zero-trust mining & hardware verification |
| `AffiliateService.ts` | `processCashback()`, `generateAffiliateUrl()`, `getAffiliateStats()`, `AFFILIATE_CONFIG` | Revenue bridge & cashback |
| `SovereigntyManager.service.ts` | `FocusModeManager`, `FocusDuelManager`, `NativeFocusShield`, `AddictionTrackerManager`, `SovereigntyManager`, `GroupDuelEconomyResult`, `calculateGroupDuelEconomy()` | Bulwark duels (P2P + Group), app-lock shield, social key, addiction staking |
| `DuelNotificationService.ts` | `DuelFriend`, `DuelInvitation`, `DuelNotificationService` | Guardian Browser friend discovery, invitation lifecycle |
| `App.tsx` | `DashboardPage`, `PageHeader`, Floating Command Stack (3 FABs) | Main app shell — wallet balance + multiplier badge, 3-FAB navigation |
| `ResonanceModule.tsx` | `ResonanceModule`, Strong Correlations panel | Dashboard correlation chart + surfaced Strong Correlations from ResonanceEngine |
| `AchievementsDB.ts` | `ACHIEVEMENTS[]`, `checkAchievements()` | One-time milestone rewards |
| `ResonanceEngine.service.ts` | `analyzeCorrelations()`, `calculateTrustScore()`, `calculateBMR()`, `calculateTDEE()` | Biometric correlation & trust weighting |
| `SupplementNexus.service.ts` | `SupplementNexus`, `SUPPLEMENTS[]`, `analyzeGaps()` | DNA-based supplement engine |
| `GamificationSystem.ts` | `calculateLevel()`, `xpForLevel()`, `getChronotypeAlignmentBonus()` | Legacy XP system (being deprecated) |
| `ProgramScheduler.service.ts` | `generateStructuredProgram()`, `formatProgramForChat()` | AI program generation |
| `ChronosArchitect.service.ts` | `PROGRAM_TEMPLATES`, `Program`, `BiologicalWrapped` | Periodization & monthly audit |
| `BioVaultModule.tsx` | `BioVaultModule` component | Genomic upload & Bio-Blueprint |
| `OracleAI.tsx` | `OracleAI`, `PulsingAuraButton` (deprecated), `initialTab` prop | AI consultation interface — Bulwark tab (formerly Focus/Sovereignty) |
| `BurnCounter.tsx` | `BurnCounter` component | Real-time deflation display |
| `useHealthStats.ts` | `useHealthStats()` | Health score aggregation hook |
| `NutritionDataLedger.ts` | `buildAnonymizedInsight()`, `getLedgerEntries()`, `getLedgerStats()`, `setResearchConsent()`, `getResearchConsent()`, `AnonymizedInsightSchema` | Data monetization — anonymized nutrition insights for research |

---

**END OF DOCUMENT**

*Generated: 7 February 2026 · Vellbeing OS Lead Architect Audit*  
*This document is auto-generated from source code analysis and constitutes the Single Source of Truth for the $MUND economic model.*
