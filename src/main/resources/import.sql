CREATE TABLE "Person" (
    "national_id" varchar PRIMARY KEY, -- Unique and Primary
    "first_name" varchar NOT NULL CHECK (char_length("first_name") > 0), -- Non-blank
    "middle_name" varchar NOT NULL CHECK (char_length("first_name") > 0), -- Non-blank
    "last_name" varchar NOT NULL CHECK (char_length("last_name") > 0), -- Non-blank
    "gender" varchar NOT NULL CHECK ("gender" IN ('Male', 'Female')), -- Enum validation
    "date_of_birth" date NOT NULL
);

CREATE TABLE "User" (
    "id" varchar PRIMARY KEY, -- Links to Person's national_id
    "username" varchar UNIQUE NOT NULL CHECK (char_length("username") > 0), -- Unique and Non-blank
    "role" varchar NOT NULL CHECK ("role" IN ('ADMIN', 'REGISTRAR', 'OFFICIAL')), -- Enum validation
    "password" varchar NOT NULL, -- Password hashing handled at the application level
    "created_at" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "Event" (
    "id" serial PRIMARY KEY, -- Serial ensures auto-increment
    "date" date NOT NULL,
    "type" varchar NOT NULL CHECK (
        "type" IN ('BIRTH', 'DEATH', 'MARRIAGE', 'DIVORCE')
    ), -- Enum validation
    "status" varchar NOT NULL CHECK ("status" IN ('PENDING', 'APPROVED', 'REJECTED')), -- Enum validation
    "location_id" integer NOT NULL,
    "registrar_id" varchar NOT NULL
);

CREATE TABLE "BirthEvent" (
    "id" integer PRIMARY KEY REFERENCES "Event" ("id"), -- Matches Event ID
    "child_id" varchar NOT NULL REFERENCES "Person" ("national_id"),
    "father_id" varchar NOT NULL REFERENCES "Person" ("national_id"), -- Optional for flexibility
    "mother_id" varchar NOT NULL REFERENCES "Person" ("national_id"), -- Optional for flexibility
    "birth_weight" float CHECK ("birth_weight" > 0) -- Weight must be positive
);

CREATE TABLE "DeathEvent" (
    "id" integer PRIMARY KEY REFERENCES "Event" ("id"),
    "deceased_id" varchar NOT NULL REFERENCES "Person" ("national_id"),
    "cause_of_death" varchar NOT NULL,
    "certifier_id" varchar REFERENCES "Person" ("national_id")
);

CREATE TABLE "MarriageEvent" (
    "id" integer PRIMARY KEY REFERENCES "Event" ("id"),
    "male_spouse_id" varchar NOT NULL REFERENCES "Person" ("national_id"),
    "female_spouse_id" varchar NOT NULL REFERENCES "Person" ("national_id"),
    "witness_one_id" varchar REFERENCES "Person" ("national_id"), -- Optional
    "witness_two_id" varchar REFERENCES "Person" ("national_id"), -- Optional
    "witness_three_id" varchar REFERENCES "Person" ("national_id") -- Optional
);

CREATE TABLE "DivorceEvent" (
    "id" integer PRIMARY KEY REFERENCES "Event" ("id"),
    "male_spouse_id" varchar NOT NULL REFERENCES "Person" ("national_id"),
    "female_spouse_id" varchar NOT NULL REFERENCES "Person" ("national_id"),
    "court_id" integer NOT NULL REFERENCES "Court" ("id")
);

CREATE TABLE "Region" (
    "id" serial PRIMARY KEY,
    "name" varchar UNIQUE NOT NULL CHECK (char_length("name") > 0), -- Unique and Non-blank
    "type" varchar NOT NULL CHECK ("type" IN ('REGION', 'CITY ADMINISTRATION')) -- Enum validation
);

CREATE TABLE "Location" (
    "id" serial PRIMARY KEY,
    "country" varchar NOT NULL DEFAULT 'Ethiopia', -- Default country
    "region_id" integer NOT NULL REFERENCES "Region" ("id"),
    "zone" varchar NOT NULL CHECK (char_length("zone") > 0),
    "woreda" varchar NOT NULL CHECK (char_length("woreda") > 0),
    "kebele" varchar NOT NULL CHECK (char_length("kebele") > 0),
    UNIQUE ("region_id", "zone", "woreda", "kebele") -- Prevents redundant locations
);

CREATE TABLE "Court" (
    "id" serial PRIMARY KEY,
    "name" varchar UNIQUE NOT NULL CHECK (char_length("name") > 0), -- Unique and Non-blank
    "jurisdiction" varchar NOT NULL CHECK (
        "jurisdiction" IN ('FEDERAL', 'REGIONAL', 'MUNICIPAL')
    ), -- Enum validation
    "type" varchar NOT NULL CHECK ("type" IN ('CIVIL', 'SHARIA', 'CUSTOMARY')),
    "location_id" integer NOT NULL REFERENCES "Location" ("id") -- Enum validation
);

CREATE TABLE "Report" (
    "id" serial PRIMARY KEY,
    "generated_by" varchar NOT NULL REFERENCES "User" ("username"),
    "generated_at" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "data" json NOT NULL -- Ensures reports always have content
);

-- Foreign Keys
-- User
ALTER TABLE "User" ADD FOREIGN KEY ("id") REFERENCES "Person" ("national_id");

-- Event
ALTER TABLE "Event" ADD FOREIGN KEY ("registrar_id") REFERENCES "User" ("username");

ALTER TABLE "Event" ADD FOREIGN KEY ("location_id") REFERENCES "Location" ("id");

-- Location
ALTER TABLE "Location" ADD FOREIGN KEY ("region_id") REFERENCES "Region" ("id");

-- Court
ALTER TABLE "Court" ADD FOREIGN KEY ("location_id") REFERENCES "Location" ("id");

-- Report
ALTER TABLE "Report" ADD FOREIGN KEY ("generated_by") REFERENCES "User" ("username");

-- BirthEvent
ALTER TABLE "BirthEvent" ADD FOREIGN KEY ("id") REFERENCES "Event" ("id");

ALTER TABLE "BirthEvent" ADD FOREIGN KEY ("child_id") REFERENCES "Person" ("national_id");

ALTER TABLE "BirthEvent" ADD FOREIGN KEY ("father_id") REFERENCES "Person" ("national_id");

ALTER TABLE "BirthEvent" ADD FOREIGN KEY ("mother_id") REFERENCES "Person" ("national_id");

-- DeathEvent
ALTER TABLE "DeathEvent" ADD FOREIGN KEY ("id") REFERENCES "Event" ("id");

ALTER TABLE "DeathEvent" ADD FOREIGN KEY ("deceased_id") REFERENCES "Person" ("national_id");

ALTER TABLE "DeathEvent" ADD FOREIGN KEY ("certifier_id") REFERENCES "Person" ("national_id");

-- MarriageEvent
ALTER TABLE "MarriageEvent" ADD FOREIGN KEY ("id") REFERENCES "Event" ("id");

ALTER TABLE "MarriageEvent" ADD FOREIGN KEY ("male_spouse_id") REFERENCES "Person" ("national_id");

ALTER TABLE "MarriageEvent" ADD FOREIGN KEY ("female_spouse_id") REFERENCES "Person" ("national_id");

ALTER TABLE "MarriageEvent" ADD FOREIGN KEY ("witness_one_id") REFERENCES "Person" ("national_id");

ALTER TABLE "MarriageEvent" ADD FOREIGN KEY ("witness_two_id") REFERENCES "Person" ("national_id");

ALTER TABLE "MarriageEvent" ADD FOREIGN KEY ("witness_three_id") REFERENCES "Person" ("national_id");

-- DivorceEvent
ALTER TABLE "DivorceEvent" ADD FOREIGN KEY ("id") REFERENCES "Event" ("id");

ALTER TABLE "DivorceEvent" ADD FOREIGN KEY ("male_spouse_id") REFERENCES "Person" ("national_id");

ALTER TABLE "DivorceEvent" ADD FOREIGN KEY ("female_spouse_id") REFERENCES "Person" ("national_id");

ALTER TABLE "DivorceEvent" ADD FOREIGN KEY ("court_id") REFERENCES "Court" ("id");
