-- V1__Initial_Schema.sql
-- Defines the database schema based on the CURRENT Java entities.

CREATE TABLE IF NOT EXISTS public.roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS public.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    reset_token VARCHAR(255),
    reset_token_expiry TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS public.user_roles (
    user_id BIGINT NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES public.roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS public.quiz (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    -- ONLY include the column that exists in Quiz.java
    time_per_question_in_seconds INTEGER NOT NULL DEFAULT 30
    -- REMOVED the old time_limit_in_seconds column
);

CREATE TABLE IF NOT EXISTS public.question (
    id BIGSERIAL PRIMARY KEY,
    content TEXT,
    correct_answer_index INTEGER NOT NULL,
    quiz_id BIGINT REFERENCES public.quiz(id) ON DELETE CASCADE
);

-- Using the join table structure created by Hibernate's @ElementCollection
CREATE TABLE IF NOT EXISTS public.question_options (
    question_id BIGINT NOT NULL REFERENCES public.question(id) ON DELETE CASCADE,
    options VARCHAR(255) -- Stores each option string
    -- Note: You might need a primary key depending on your exact previous schema
    -- e.g., PRIMARY KEY (question_id, options) if options must be unique per question
);

CREATE TABLE IF NOT EXISTS public.result (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES public.users(id),
    quiz_id BIGINT REFERENCES public.quiz(id),
    score INTEGER NOT NULL,
    total_correct INTEGER NOT NULL,
    total_questions INTEGER NOT NULL,
    submission_time TIMESTAMP WITHOUT TIME ZONE
);

-- Optional but recommended indexes
CREATE INDEX IF NOT EXISTS idx_user_email ON public.users(email);
CREATE INDEX IF NOT EXISTS idx_user_reset_token ON public.users(reset_token);
