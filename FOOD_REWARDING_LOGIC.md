# MUNDIR Food Rewarding Logic

## Current Implementation

### Simple Flat Rate Model
```
Every meal logged = 5 $MUND (€0.005)

Rate limits:
- Max 3 meals/day = 15 $MUND/day (€0.015)
- Must wait 3 hours between meals (prevents spam)
- No quality scoring (eating junk = same as eating healthy)
```

### How It Works
```
User action: "Log Meal"
         ↓
Check daily limit (< 3 meals?)
         ↓
Check 3-hour gap since last meal
         ↓
If valid: Award 5 $MUND
If invalid: Show "Wait X minutes" message
```

### Problem with Current Model
❌ **No nutritional incentives:**
- Eating a McDonald's burger = 5 $MUND
- Eating a salad with grilled chicken = 5 $MUND
- Eating nothing for 3 hours = 0 $MUND (should be 0!)

❌ **No macro tracking:**
- High protein meal (good) = 5 $MUND
- High sugar meal (bad) = 5 $MUND
- High carb meal (depends on goals) = 5 $MUND

❌ **Perverse incentives:**
- User has no reason to eat BETTER, just to eat MORE often
- Every 3 hours they get rewarded just for existing + logging

---

## Proposed Improved Model

### Option 1: Macro-Based Rewards (SIMPLE)

```typescript
// Reward bonus based on protein percentage
const calculateMealBonus = (macros: {protein: number, carbs: number, fat: number}) => {
  const total = macros.protein + macros.carbs + macros.fat;
  const proteinPercent = (macros.protein / total) * 100;
  
  if (proteinPercent >= 35) return 8;      // High protein = +3 bonus
  if (proteinPercent >= 25) return 5;      // Normal = base 5
  if (proteinPercent < 20) return 2;       // Low protein = only 2
}

// Usage:
// Lunch: Chicken + rice (35g P, 50g C, 15g F) = 50% protein → 8 $MUND
// Lunch: Burger + fries (15g P, 60g C, 30g F) = 16% protein → 2 $MUND
```

**Pros:**
- Easy to implement
- Incentivizes high-protein (muscle building)
- Works with existing nutrition tracking

**Cons:**
- Ignores micronutrients (vitamins, minerals)
- Ignores fiber (satiety)
- Bodybuilders get 40% bonuses, vegans struggle

---

### Option 2: Balanced Nutrition Score (SOPHISTICATED)

```typescript
const calculateNutritionScore = (meal: MealEntry): {score: 0-10, bonus: 0-5} => {
  let score = 0;
  
  // Calorie appropriateness (±500 of daily goal)
  const target = 2000; // adjust per user
  if (meal.calories >= target * 0.4 && meal.calories <= target * 0.6) {
    score += 2; // Good portion size
  }
  
  // Protein quality (grams per calorie)
  const proteinRatio = meal.protein / meal.calories;
  if (proteinRatio >= 0.3) score += 2;      // 30%+ protein = excellent
  else if (proteinRatio >= 0.2) score += 1; // 20%+ = good
  
  // Fiber (promotes digestive health)
  if (meal.fiber >= 8) score += 1;
  
  // Vegetables (micronutrients)
  if (meal.veggie_servings >= 2) score += 1;
  
  // Avoid ultra-processed (sugar content)
  const sugarRatio = meal.sugar / meal.calories;
  if (sugarRatio <= 0.05) score += 2;       // <5% sugar = excellent
  else if (sugarRatio > 0.15) score -= 1;   // >15% = penalty
  
  // Convert score (0-10) to bonus ($MUND)
  const bonus = Math.floor(score / 2);      // 0-5 bonus
  
  return { score: Math.min(10, Math.max(0, score)), bonus };
}

// Usage:
// Lunch: Grilled salmon, quinoa, broccoli, olive oil
//   Calories: 550, Protein: 45g (33%), Fiber: 8g, Veg: 2 servings, Sugar: 2g
//   → Score: 9/10, Bonus: +4 → Total: 5 base + 4 bonus = 9 $MUND

// Lunch: Big Mac, fries, Coke
//   Calories: 1100, Protein: 25g (9%), Fiber: 1g, Veg: 0, Sugar: 65g (24%)
//   → Score: 1/10, Bonus: 0 → Total: 5 base = 5 $MUND
```

**Pros:**
- Incentivizes ACTUAL healthy eating
- Adapts to different diets (low-carb, vegan, etc.)
- Teaches users what "healthy" looks like
- Educational value

**Cons:**
- Need detailed nutrition data (requires barcode scanning or manual entry)
- More complex implementation
- Requires nutrition database integration

---

### Option 3: Hybrid (RECOMMENDED)

```typescript
// Simple version: protein-only bonus
const mealReward = 5; // Base

if (macros.proteinPercent >= 30) {
  mealReward += 3; // "High-protein" bonus
} else if (macros.proteinPercent >= 20) {
  mealReward += 1; // "Good" bonus
} else {
  mealReward += 0; // No bonus
}

// Result: 5-8 $MUND depending on protein content
```

---

## Integration with NutritionModule

### Current NutritionModule tracks:
- Calories in/out
- Protein, carbs, fats
- Vegetable servings
- Supplement intake

### What it DOESN'T do:
- Award $MUND for logging meals (that's in MundContext only)
- Track meal quality
- Give real-time feedback on macros

### Proposed Enhancement:
```
Step 1: User logs meal in Nutrition tab
Step 2: System reads macros from nutrition DB
Step 3: Calculate nutrition score (0-10)
Step 4: Award base 5 $MUND + nutrition bonus (0-3)
Step 5: Show toast: "✅ Meal logged! +7 $MUND (High Protein +2)"
Step 6: Update nutrition dashboard with streak
```

---

## Recommended Implementation

### MVP (Minimum Viable):
```
Award structure:
- Every meal: 5 $MUND base (prevents gaming by logging empty meals)
- Protein bonus: +3 $MUND if protein ≥ 35%
- High fiber bonus: +2 $MUND if fiber ≥ 8g
- Max: 10 $MUND per meal

Formula:
base = 5
bonus = (proteinPercent >= 35 ? 3 : 0) + (fiber >= 8 ? 2 : 0)
total = min(10, base + bonus)
```

### Why This Works:
1. **Simple:** Only requires protein % (already tracked)
2. **Incentivizes:** Encourages better eating habits
3. **Flexible:** Works with all diets
4. **Fair:** Base 5 means you get rewarded even for simple meals

### Example Meals:
| Meal | Protein | Fiber | $MUND | Notes |
|------|---------|-------|-------|-------|
| Grilled chicken breast + rice + beans | 40% | 9g | 10 | Perfect score |
| Greek yogurt + granola + berries | 28% | 4g | 5 | Base only |
| Big Mac + fries | 14% | 2g | 5 | Base only |
| Oatmeal + eggs + vegetables | 25% | 7g | 6 | High fiber |
| Turkey sandwich on whole wheat | 30% | 5g | 8 | Protein bonus |

---

## Financial Impact

### Current Model
- User logs 3 meals/day × 5 $MUND = **15 $MUND/day**
- Yearly: 15 × 365 = **5,475 $MUND/year** (€5.48)

### With Protein Bonus
- User logs 3 healthy meals/day × 8 $MUND avg = **24 $MUND/day**
- Yearly: 24 × 365 = **8,760 $MUND/year** (€8.76)

**Impact:** +60% more $MUND payout, but:
- Users get better nutrition outcomes → Better retention
- Users feel rewarded for "doing it right" → Higher engagement
- Marketing angle: "Get paid more for eating healthy"

---

## Implementation Roadmap

### Phase 1 (Current): 
✓ Simple flat 5 $MUND per meal

### Phase 2 (Recommended):
- Add protein bonus (3 hours of dev)
- Update NutritionModule to show $MUND rewards
- Test with 50 beta users

### Phase 3 (Advanced):
- Add fiber tracking
- Integrate full nutrition scoring
- Show "meal insights" (e.g., "Your last 7 meals averaged 28% protein")

### Phase 4 (Future):
- Partner with fitness coaches (Telegram/Discord)
- Let coaches set meal bonus multipliers
- "Coach's Challenge" tournaments

---

## My Recommendation

**Start with Protein Bonus (Phase 2) before launch:**
- Easy to implement (2-3 hours)
- Clear value proposition ("Earn up to 8 $MUND per healthy meal")
- Improves retention (users see tangible reward for good choices)
- Low cost (maybe +1% additional $MUND payout)

**Don't use basic model (current):**
- Too simplistic
- No incentive to eat well
- Missed opportunity for user education

Would you like me to implement the protein-based reward system in the code?
