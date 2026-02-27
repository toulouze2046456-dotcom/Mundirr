# Vellbeing OS 🌿

A comprehensive wellness tracking application that helps you monitor and improve multiple dimensions of your health.

## Features

- **🏃 Physical Health** - Track workouts, activities, and calories burned
- **🍎 Nutrition** - Log meals with macro tracking (calories, protein, fat, carbs)
- **💊 Supplements** - Manage your supplement stack with daily check-offs
- **☕ Substances** - Monitor caffeine, alcohol, and other substances
- **😴 Sleep** - Track sleep duration and quality
- **💰 Finance** - Log income and expenses for financial wellness
- **📚 Cultural** - Track reading, language learning, and enrichment activities

## Tech Stack

### Backend
- **Java 17** with **Spring Boot 3.2**
- **SQLite** database with JPA/Hibernate
- **JWT Authentication** with stateless sessions
- **Spring Security** for protected endpoints
- **JavaMail** for email notifications

### Frontend
- **React 18** with **TypeScript**
- **Vite 5** for fast development
- **Tailwind CSS** for styling
- **Lucide React** for icons
- **Recharts** for data visualization

## Project Structure

```
vellbeing-os/
├── src/main/java/com/health/
│   ├── Main.java                 # Spring Boot entry point
│   ├── entity/                   # JPA entities
│   ├── repository/               # Data access layer
│   ├── service/                  # Business logic
│   ├── controller/               # REST API endpoints
│   └── security/                 # JWT & Spring Security
├── src/main/resources/
│   └── application.properties    # App configuration
├── client/                       # React frontend
│   ├── src/
│   │   ├── api/client.ts         # API client
│   │   ├── hooks/useAuth.ts      # Auth hook
│   │   └── components/           # React components
│   ├── package.json
│   └── vite.config.ts
└── pom.xml                       # Maven dependencies
```

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- Maven 3.8+

### Backend Setup

1. Navigate to project root:
```bash
cd vellbeing-os
```

2. Install dependencies and run:
```bash
mvn spring-boot:run
```

The backend will start at `http://localhost:8080`

### Frontend Setup

1. Navigate to client directory:
```bash
cd client
```

2. Install dependencies:
```bash
npm install
```

3. Start development server:
```bash
npm run dev
```

The frontend will start at `http://localhost:5173`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Create new account
- `POST /api/auth/login` - Login and get JWT token
- `POST /api/auth/forgot-password` - Request password reset email
- `POST /api/auth/reset-password` - Reset password with token

### Physical Health
- `GET /api/activities?date=YYYY-MM-DD` - Get activities by date
- `POST /api/activities` - Log new activity
- `DELETE /api/activities/{id}` - Delete activity

### Nutrition
- `GET /api/food?date=YYYY-MM-DD` - Get foods by date
- `POST /api/food` - Log food entry
- `DELETE /api/food/{id}` - Delete food entry

### Supplements
- `GET /api/supplements` - Get all supplements
- `POST /api/supplements` - Add supplement
- `POST /api/supplements/{id}/log` - Log supplement intake
- `GET /api/supplements/logs?date=YYYY-MM-DD` - Get intake logs

### Sleep
- `GET /api/sleep?date=YYYY-MM-DD` - Get sleep logs
- `POST /api/sleep` - Log sleep entry

### Finance
- `GET /api/finance?month=YYYY-MM` - Get transactions by month
- `POST /api/finance` - Add transaction

### Gamification
- `GET /api/gamification/health-scores` - Get all health dimension scores

## Configuration

### Environment Variables

Configure these in `application.properties`:

```properties
# JWT Configuration
jwt.secret=your-256-bit-secret-key
jwt.expiration=86400000

# Email Configuration (for password reset)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# CORS (for development)
app.cors.allowed-origins=http://localhost:5173
```

## Demo Account

For testing, a demo account is created on first run:
- **Email:** demo@vellbeing.com
- **Password:** demo123

## Future Enhancements (Phase 2 & 3)

- **USDA API Integration** - Auto-populate nutrition data
- **Wger API Integration** - Exercise database with calories estimation
- **Hormonal Impact Tracking** - Scientific coefficients for substance effects
- **Data Export** - CSV/PDF reports
- **Mobile Responsive** - PWA support
- **Charts & Analytics** - Weekly/monthly trends

## License

MIT License - feel free to use and modify!
# Mundirr
