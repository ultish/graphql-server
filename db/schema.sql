--
-- PostgreSQL database dump
--

-- Dumped from database version 12.3 (Debian 12.3-1.pgdg100+1)
-- Dumped by pg_dump version 12.2

-- Started on 2020-08-26 16:12:48 AEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 207 (class 1259 OID 90303)
-- Name: chargecodes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chargecodes (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(255) NOT NULL,
    description character varying(255),
    expired boolean NOT NULL,
    "createdAt" timestamp with time zone NOT NULL,
    "updatedAt" timestamp with time zone NOT NULL
);


ALTER TABLE public.chargecodes OWNER TO postgres;

--
-- TOC entry 206 (class 1259 OID 90301)
-- Name: chargecodes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.chargecodes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.chargecodes_id_seq OWNER TO postgres;

--
-- TOC entry 3015 (class 0 OID 0)
-- Dependencies: 206
-- Name: chargecodes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.chargecodes_id_seq OWNED BY public.chargecodes.id;


--
-- TOC entry 205 (class 1259 OID 90289)
-- Name: messages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.messages (
    id integer NOT NULL,
    text character varying(255),
    "createdAt" timestamp with time zone NOT NULL,
    "updatedAt" timestamp with time zone NOT NULL,
    "userId" integer
);


ALTER TABLE public.messages OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 90287)
-- Name: messages_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.messages_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.messages_id_seq OWNER TO postgres;

--
-- TOC entry 3016 (class 0 OID 0)
-- Dependencies: 204
-- Name: messages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.messages_id_seq OWNED BY public.messages.id;


--
-- TOC entry 218 (class 1259 OID 90419)
-- Name: taskcodes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.taskcodes (
    "chargecodeId" integer NOT NULL,
    "trackedtaskId" integer NOT NULL,
    "updatedAt" timestamp with time zone,
    "createdAt" timestamp with time zone
);


ALTER TABLE public.taskcodes OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 90373)
-- Name: timeblocks; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.timeblocks (
    id integer NOT NULL,
    "startTime" timestamp with time zone NOT NULL,
    "createdAt" timestamp with time zone NOT NULL,
    "updatedAt" timestamp with time zone NOT NULL,
    "trackedtaskId" integer,
    "userId" integer
);


ALTER TABLE public.timeblocks OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 90371)
-- Name: timeblocks_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.timeblocks_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.timeblocks_id_seq OWNER TO postgres;

--
-- TOC entry 3017 (class 0 OID 0)
-- Dependencies: 214
-- Name: timeblocks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.timeblocks_id_seq OWNED BY public.timeblocks.id;


--
-- TOC entry 217 (class 1259 OID 90394)
-- Name: timecharges; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.timecharges (
    id integer NOT NULL,
    date timestamp with time zone NOT NULL,
    value double precision,
    mode character varying(255),
    "createdAt" timestamp with time zone NOT NULL,
    "updatedAt" timestamp with time zone NOT NULL,
    "trackeddayId" integer,
    "timesheetId" integer,
    "chargecodeId" integer
);


ALTER TABLE public.timecharges OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 90392)
-- Name: timecharges_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.timecharges_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.timecharges_id_seq OWNER TO postgres;

--
-- TOC entry 3018 (class 0 OID 0)
-- Dependencies: 216
-- Name: timecharges_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.timecharges_id_seq OWNED BY public.timecharges.id;


--
-- TOC entry 209 (class 1259 OID 90318)
-- Name: timesheets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.timesheets (
    id integer NOT NULL,
    "weekEndingDate" timestamp with time zone NOT NULL,
    "createdAt" timestamp with time zone NOT NULL,
    "updatedAt" timestamp with time zone NOT NULL,
    "userId" integer
);


ALTER TABLE public.timesheets OWNER TO postgres;

--
-- TOC entry 208 (class 1259 OID 90316)
-- Name: timesheets_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.timesheets_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.timesheets_id_seq OWNER TO postgres;

--
-- TOC entry 3019 (class 0 OID 0)
-- Dependencies: 208
-- Name: timesheets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.timesheets_id_seq OWNED BY public.timesheets.id;


--
-- TOC entry 211 (class 1259 OID 90332)
-- Name: trackeddays; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trackeddays (
    id integer NOT NULL,
    date timestamp with time zone NOT NULL,
    mode character varying(255) NOT NULL,
    "createdAt" timestamp with time zone NOT NULL,
    "updatedAt" timestamp with time zone NOT NULL,
    "userId" integer,
    "timesheetId" integer
);


ALTER TABLE public.trackeddays OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 90330)
-- Name: trackeddays_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.trackeddays_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.trackeddays_id_seq OWNER TO postgres;

--
-- TOC entry 3020 (class 0 OID 0)
-- Dependencies: 210
-- Name: trackeddays_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.trackeddays_id_seq OWNED BY public.trackeddays.id;


--
-- TOC entry 213 (class 1259 OID 90353)
-- Name: trackedtasks; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trackedtasks (
    id integer NOT NULL,
    notes character varying(255),
    "overtimeEnabled" boolean,
    "createdAt" timestamp with time zone NOT NULL,
    "updatedAt" timestamp with time zone NOT NULL,
    "trackeddayId" integer,
    "userId" integer
);


ALTER TABLE public.trackedtasks OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 90351)
-- Name: trackedtasks_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.trackedtasks_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.trackedtasks_id_seq OWNER TO postgres;

--
-- TOC entry 3021 (class 0 OID 0)
-- Dependencies: 212
-- Name: trackedtasks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.trackedtasks_id_seq OWNED BY public.trackedtasks.id;


--
-- TOC entry 203 (class 1259 OID 90276)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer NOT NULL,
    username character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    email character varying(255),
    role character varying(255),
    "createdAt" timestamp with time zone NOT NULL,
    "updatedAt" timestamp with time zone NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 90274)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO postgres;

--
-- TOC entry 3022 (class 0 OID 0)
-- Dependencies: 202
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 2827 (class 2604 OID 90306)
-- Name: chargecodes id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chargecodes ALTER COLUMN id SET DEFAULT nextval('public.chargecodes_id_seq'::regclass);


--
-- TOC entry 2826 (class 2604 OID 90292)
-- Name: messages id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages ALTER COLUMN id SET DEFAULT nextval('public.messages_id_seq'::regclass);


--
-- TOC entry 2831 (class 2604 OID 90376)
-- Name: timeblocks id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.timeblocks ALTER COLUMN id SET DEFAULT nextval('public.timeblocks_id_seq'::regclass);


--
-- TOC entry 2832 (class 2604 OID 90397)
-- Name: timecharges id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.timecharges ALTER COLUMN id SET DEFAULT nextval('public.timecharges_id_seq'::regclass);


--
-- TOC entry 2828 (class 2604 OID 90321)
-- Name: timesheets id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.timesheets ALTER COLUMN id SET DEFAULT nextval('public.timesheets_id_seq'::regclass);


--
-- TOC entry 2829 (class 2604 OID 90335)
-- Name: trackeddays id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trackeddays ALTER COLUMN id SET DEFAULT nextval('public.trackeddays_id_seq'::regclass);


--
-- TOC entry 2830 (class 2604 OID 90356)
-- Name: trackedtasks id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trackedtasks ALTER COLUMN id SET DEFAULT nextval('public.trackedtasks_id_seq'::regclass);


--
-- TOC entry 2825 (class 2604 OID 90279)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 2841 (class 2606 OID 90315)
-- Name: chargecodes chargecodes_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chargecodes
    ADD CONSTRAINT chargecodes_code_key UNIQUE (code);


--
-- TOC entry 2843 (class 2606 OID 90313)
-- Name: chargecodes chargecodes_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chargecodes
    ADD CONSTRAINT chargecodes_name_key UNIQUE (name);


--
-- TOC entry 2845 (class 2606 OID 90311)
-- Name: chargecodes chargecodes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chargecodes
    ADD CONSTRAINT chargecodes_pkey PRIMARY KEY (id);


--
-- TOC entry 2838 (class 2606 OID 90294)
-- Name: messages messages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);


--
-- TOC entry 2870 (class 2606 OID 90423)
-- Name: taskcodes taskcodes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.taskcodes
    ADD CONSTRAINT taskcodes_pkey PRIMARY KEY ("chargecodeId", "trackedtaskId");


--
-- TOC entry 2859 (class 2606 OID 90378)
-- Name: timeblocks timeblocks_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.timeblocks
    ADD CONSTRAINT timeblocks_pkey PRIMARY KEY (id);


--
-- TOC entry 2866 (class 2606 OID 90399)
-- Name: timecharges timecharges_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.timecharges
    ADD CONSTRAINT timecharges_pkey PRIMARY KEY (id);


--
-- TOC entry 2847 (class 2606 OID 90323)
-- Name: timesheets timesheets_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.timesheets
    ADD CONSTRAINT timesheets_pkey PRIMARY KEY (id);


--
-- TOC entry 2850 (class 2606 OID 90337)
-- Name: trackeddays trackeddays_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trackeddays
    ADD CONSTRAINT trackeddays_pkey PRIMARY KEY (id);


--
-- TOC entry 2855 (class 2606 OID 90358)
-- Name: trackedtasks trackedtasks_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trackedtasks
    ADD CONSTRAINT trackedtasks_pkey PRIMARY KEY (id);


--
-- TOC entry 2834 (class 2606 OID 90284)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 2836 (class 2606 OID 90286)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 2839 (class 1259 OID 90300)
-- Name: messages_user_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX messages_user_id ON public.messages USING btree ("userId");


--
-- TOC entry 2860 (class 1259 OID 90389)
-- Name: timeblocks_start_time_trackedtask_id_user_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX timeblocks_start_time_trackedtask_id_user_id ON public.timeblocks USING btree ("startTime", "trackedtaskId", "userId");


--
-- TOC entry 2861 (class 1259 OID 90390)
-- Name: timeblocks_trackedtask_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX timeblocks_trackedtask_id ON public.timeblocks USING btree ("trackedtaskId");


--
-- TOC entry 2862 (class 1259 OID 90391)
-- Name: timeblocks_user_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX timeblocks_user_id ON public.timeblocks USING btree ("userId");


--
-- TOC entry 2863 (class 1259 OID 90417)
-- Name: timecharges_chargecode_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX timecharges_chargecode_id ON public.timecharges USING btree ("chargecodeId");


--
-- TOC entry 2864 (class 1259 OID 90418)
-- Name: timecharges_date; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX timecharges_date ON public.timecharges USING btree (date);


--
-- TOC entry 2867 (class 1259 OID 90416)
-- Name: timecharges_timesheet_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX timecharges_timesheet_id ON public.timecharges USING btree ("timesheetId");


--
-- TOC entry 2868 (class 1259 OID 90415)
-- Name: timecharges_trackedday_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX timecharges_trackedday_id ON public.timecharges USING btree ("trackeddayId");


--
-- TOC entry 2848 (class 1259 OID 90329)
-- Name: timesheets_user_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX timesheets_user_id ON public.timesheets USING btree ("userId");


--
-- TOC entry 2851 (class 1259 OID 90349)
-- Name: trackeddays_timesheet_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX trackeddays_timesheet_id ON public.trackeddays USING btree ("timesheetId");


--
-- TOC entry 2852 (class 1259 OID 90348)
-- Name: trackeddays_user_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX trackeddays_user_id ON public.trackeddays USING btree ("userId");


--
-- TOC entry 2853 (class 1259 OID 90350)
-- Name: trackeddays_user_id_date; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX trackeddays_user_id_date ON public.trackeddays USING btree ("userId", date);


--
-- TOC entry 2856 (class 1259 OID 90369)
-- Name: trackedtasks_trackedday_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX trackedtasks_trackedday_id ON public.trackedtasks USING btree ("trackeddayId");


--
-- TOC entry 2857 (class 1259 OID 90370)
-- Name: trackedtasks_user_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX trackedtasks_user_id ON public.trackedtasks USING btree ("userId");


--
-- TOC entry 2871 (class 2606 OID 90295)
-- Name: messages messages_userId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT "messages_userId_fkey" FOREIGN KEY ("userId") REFERENCES public.users(id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 2882 (class 2606 OID 90424)
-- Name: taskcodes taskcodes_chargecodeId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.taskcodes
    ADD CONSTRAINT "taskcodes_chargecodeId_fkey" FOREIGN KEY ("chargecodeId") REFERENCES public.chargecodes(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2883 (class 2606 OID 90429)
-- Name: taskcodes taskcodes_trackedtaskId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.taskcodes
    ADD CONSTRAINT "taskcodes_trackedtaskId_fkey" FOREIGN KEY ("trackedtaskId") REFERENCES public.trackedtasks(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2877 (class 2606 OID 90379)
-- Name: timeblocks timeblocks_trackedtaskId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.timeblocks
    ADD CONSTRAINT "timeblocks_trackedtaskId_fkey" FOREIGN KEY ("trackedtaskId") REFERENCES public.trackedtasks(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2878 (class 2606 OID 90384)
-- Name: timeblocks timeblocks_userId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.timeblocks
    ADD CONSTRAINT "timeblocks_userId_fkey" FOREIGN KEY ("userId") REFERENCES public.users(id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 2881 (class 2606 OID 90410)
-- Name: timecharges timecharges_chargecodeId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.timecharges
    ADD CONSTRAINT "timecharges_chargecodeId_fkey" FOREIGN KEY ("chargecodeId") REFERENCES public.chargecodes(id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 2880 (class 2606 OID 90405)
-- Name: timecharges timecharges_timesheetId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.timecharges
    ADD CONSTRAINT "timecharges_timesheetId_fkey" FOREIGN KEY ("timesheetId") REFERENCES public.timesheets(id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 2879 (class 2606 OID 90400)
-- Name: timecharges timecharges_trackeddayId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.timecharges
    ADD CONSTRAINT "timecharges_trackeddayId_fkey" FOREIGN KEY ("trackeddayId") REFERENCES public.trackeddays(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2872 (class 2606 OID 90324)
-- Name: timesheets timesheets_userId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.timesheets
    ADD CONSTRAINT "timesheets_userId_fkey" FOREIGN KEY ("userId") REFERENCES public.users(id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 2874 (class 2606 OID 90343)
-- Name: trackeddays trackeddays_timesheetId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trackeddays
    ADD CONSTRAINT "trackeddays_timesheetId_fkey" FOREIGN KEY ("timesheetId") REFERENCES public.timesheets(id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 2873 (class 2606 OID 90338)
-- Name: trackeddays trackeddays_userId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trackeddays
    ADD CONSTRAINT "trackeddays_userId_fkey" FOREIGN KEY ("userId") REFERENCES public.users(id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 2875 (class 2606 OID 90359)
-- Name: trackedtasks trackedtasks_trackeddayId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trackedtasks
    ADD CONSTRAINT "trackedtasks_trackeddayId_fkey" FOREIGN KEY ("trackeddayId") REFERENCES public.trackeddays(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2876 (class 2606 OID 90364)
-- Name: trackedtasks trackedtasks_userId_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trackedtasks
    ADD CONSTRAINT "trackedtasks_userId_fkey" FOREIGN KEY ("userId") REFERENCES public.users(id) ON UPDATE CASCADE ON DELETE SET NULL;


-- Completed on 2020-08-26 16:12:52 AEST

--
-- PostgreSQL database dump complete
--

