/* ============================================================
   SNAPSHOT01DB - Enterprise Shareholder Management System
   MSSQL Schema - 15 bang, day du UNIQUE/CHECK constraints
   ============================================================ */

/* ------------------------------------------------------------
   1. USERS

------------------------------------------------------------ */
create database snapshot01db;
go
   
CREATE TABLE USERS (
    user_id         INT IDENTITY(1,1) PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL,
    email           VARCHAR(100) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(20)  NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    created_at      DATETIME2    NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT UQ_Users_Username UNIQUE (username),
    CONSTRAINT UQ_Users_Email    UNIQUE (email),
    CONSTRAINT CHK_Users_Role    CHECK (role IN ('ADMIN','IT','SHAREHOLDER','GUEST')),
    CONSTRAINT CHK_Users_Status  CHECK (status IN ('ACTIVE','LOCKED'))
);
GO

/* ------------------------------------------------------------
   2. SHAREHOLDERS
   ------------------------------------------------------------ */
CREATE TABLE SHAREHOLDERS (
    shareholder_id  INT IDENTITY(1,1) PRIMARY KEY,
    user_id         INT NOT NULL,
    full_name       NVARCHAR(150) NOT NULL,
    citizen_id      VARCHAR(20)   NOT NULL,
    phone           VARCHAR(20)   NULL,
    address         NVARCHAR(255) NULL,
    birth_date      DATE          NULL,
    nationality     NVARCHAR(50)  NULL,
    created_at      DATETIME2     NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_Shareholders_Users FOREIGN KEY (user_id) REFERENCES USERS(user_id),
    CONSTRAINT UQ_Shareholders_UserId    UNIQUE (user_id),      -- 1 user = 1 shareholder
    CONSTRAINT UQ_Shareholders_CitizenId UNIQUE (citizen_id)
);
GO

/* ------------------------------------------------------------
   3. SHARES  (moi shareholder chi 1 dong so du hien tai)
   ------------------------------------------------------------ */
CREATE TABLE SHARES (
    share_id        INT IDENTITY(1,1) PRIMARY KEY,
    shareholder_id  INT NOT NULL,
    quantity        INT NOT NULL DEFAULT 0,
    updated_at      DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_Shares_Shareholders FOREIGN KEY (shareholder_id) REFERENCES SHAREHOLDERS(shareholder_id),
    CONSTRAINT UQ_Shares_Shareholder  UNIQUE (shareholder_id),
    CONSTRAINT CHK_Shares_Quantity    CHECK (quantity >= 0)
);
GO

/* ------------------------------------------------------------
   4. SHARE_SNAPSHOTS
   ------------------------------------------------------------ */
CREATE TABLE SHARE_SNAPSHOTS (
    snapshot_id     INT IDENTITY(1,1) PRIMARY KEY,
    snapshot_date   DATETIME2 NOT NULL,
    reason          NVARCHAR(255) NULL,
    created_at      DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO

/* ------------------------------------------------------------
   5. SHARE_SNAPSHOT_DETAILS
   ------------------------------------------------------------ */
CREATE TABLE SHARE_SNAPSHOT_DETAILS (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    snapshot_id     INT NOT NULL,
    shareholder_id  INT NOT NULL,
    share_quantity  INT NOT NULL,

    CONSTRAINT FK_SnapDetail_Snapshot    FOREIGN KEY (snapshot_id)    REFERENCES SHARE_SNAPSHOTS(snapshot_id),
    CONSTRAINT FK_SnapDetail_Shareholder FOREIGN KEY (shareholder_id) REFERENCES SHAREHOLDERS(shareholder_id),
    CONSTRAINT UQ_SnapDetail UNIQUE (snapshot_id, shareholder_id),
    CONSTRAINT CHK_SnapDetail_Qty CHECK (share_quantity >= 0)
);
GO

/* ------------------------------------------------------------
   6. SHARE_ISSUES
   ------------------------------------------------------------ */
CREATE TABLE SHARE_ISSUES (
    issue_id        INT IDENTITY(1,1) PRIMARY KEY,
    title           NVARCHAR(200) NOT NULL,
    issue_type      VARCHAR(20)   NOT NULL,
    issue_date      DATE          NOT NULL,
    snapshot_date   DATETIME2     NOT NULL,
    share_quantity  INT           NOT NULL,
    issue_ratio     DECIMAL(10,4) NULL,
    start_date      DATETIME2     NOT NULL,
    end_date        DATETIME2     NOT NULL,
    description     NVARCHAR(MAX) NULL,
    created_at      DATETIME2     NOT NULL DEFAULT SYSDATETIME(),

    -- He thong hien chi ho tro co tuc bang co phieu (khong con "quyen mua" / issue_price).
    CONSTRAINT CHK_Issues_Type CHECK (issue_type = 'DIVIDEND'),
    CONSTRAINT CHK_Issues_ShareQty CHECK (share_quantity > 0),
    CONSTRAINT CHK_Issues_Dates CHECK (end_date > start_date),
    -- Chan nhap sai thu tu ngay: phat hanh -> chot snapshot -> mo ky -> dong ky
    CONSTRAINT CHK_Issues_DateOrder CHECK (
        issue_date <= snapshot_date
        AND snapshot_date <= start_date
        AND start_date <= end_date
    )
);
GO

/* ------------------------------------------------------------
   7. SHARE_ISSUE_DETAILS
   ------------------------------------------------------------ */
CREATE TABLE SHARE_ISSUE_DETAILS (
    id                  INT IDENTITY(1,1) PRIMARY KEY,
    issue_id            INT NOT NULL,
    snapshot_id         INT NOT NULL,
    shareholder_id      INT NOT NULL,
    eligible_quantity   INT NOT NULL,
    received_quantity   INT NOT NULL DEFAULT 0,
    signature_url       VARCHAR(500) NULL,
    signed_at           DATETIME2 NULL,
    status              VARCHAR(20) NOT NULL,

    CONSTRAINT FK_IssueDetail_Issue       FOREIGN KEY (issue_id)       REFERENCES SHARE_ISSUES(issue_id),
    CONSTRAINT FK_IssueDetail_Snapshot    FOREIGN KEY (snapshot_id)    REFERENCES SHARE_SNAPSHOTS(snapshot_id),
    CONSTRAINT FK_IssueDetail_Shareholder FOREIGN KEY (shareholder_id) REFERENCES SHAREHOLDERS(shareholder_id),
    CONSTRAINT UQ_IssueDetail UNIQUE (issue_id, shareholder_id),
    CONSTRAINT CHK_IssueDetail_Status CHECK (status IN ('PENDING','ACCEPTED','COMPLETED','EXPIRED')),
    CONSTRAINT CHK_IssueDetail_EligibleQty CHECK (eligible_quantity >= 0),
    CONSTRAINT CHK_IssueDetail_ReceivedQty CHECK (
        received_quantity >= 0
        AND received_quantity <= eligible_quantity
    )
);
GO

/* ------------------------------------------------------------
   8. SHARE_TRANSACTIONS
   ------------------------------------------------------------ */
CREATE TABLE SHARE_TRANSACTIONS (
    tx_id                INT IDENTITY(1,1) PRIMARY KEY,
    from_shareholder_id  INT NULL,
    to_shareholder_id    INT NULL,
    quantity             INT NOT NULL,
    tx_type              VARCHAR(20) NOT NULL,
    status               VARCHAR(20) NOT NULL,
    created_at           DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_Tx_From FOREIGN KEY (from_shareholder_id) REFERENCES SHAREHOLDERS(shareholder_id),
    CONSTRAINT FK_Tx_To   FOREIGN KEY (to_shareholder_id)   REFERENCES SHAREHOLDERS(shareholder_id),
    CONSTRAINT CHK_Tx_Type CHECK (tx_type IN ('INITIAL','TRANSFER','ISSUE','ADJUSTMENT')),
    CONSTRAINT CHK_Tx_Status CHECK (status IN ('PENDING','COMPLETED','REJECTED')),
    CONSTRAINT CHK_Tx_Quantity CHECK (quantity > 0),
    -- TRANSFER: co ca from & to | INITIAL, ISSUE, ADJUSTMENT: chi co to (nguon la cong ty/he thong)
    CONSTRAINT CHK_Tx_FromTo CHECK (
        (tx_type = 'TRANSFER'   AND from_shareholder_id IS NOT NULL AND to_shareholder_id IS NOT NULL)
        OR (tx_type = 'INITIAL'    AND from_shareholder_id IS NULL     AND to_shareholder_id IS NOT NULL)
        OR (tx_type = 'ISSUE'      AND from_shareholder_id IS NULL     AND to_shareholder_id IS NOT NULL)
        OR (tx_type = 'ADJUSTMENT' AND from_shareholder_id IS NULL     AND to_shareholder_id IS NOT NULL)
    )
);
GO

/* ------------------------------------------------------------
   9. RESOLUTIONS
   ------------------------------------------------------------ */
CREATE TABLE RESOLUTIONS (
    resolution_id   INT IDENTITY(1,1) PRIMARY KEY,
    title           NVARCHAR(200) NOT NULL,
    description     NVARCHAR(MAX) NULL,
    status          VARCHAR(20)   NOT NULL,
    start_time      DATETIME2     NOT NULL,
    end_time        DATETIME2     NOT NULL,
    created_at      DATETIME2     NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT CHK_Resolutions_Status CHECK (status IN ('OPEN','CLOSED')),
    CONSTRAINT CHK_Resolutions_Time CHECK (end_time > start_time)
);
GO

/* ------------------------------------------------------------
   10. VOTES
   ------------------------------------------------------------ */
CREATE TABLE VOTES (
    vote_id         INT IDENTITY(1,1) PRIMARY KEY,
    resolution_id   INT NOT NULL,
    shareholder_id  INT NOT NULL,
    snapshot_id     INT NOT NULL,
    vote_value      VARCHAR(20) NOT NULL,
    voted_at        DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_Votes_Resolution  FOREIGN KEY (resolution_id)  REFERENCES RESOLUTIONS(resolution_id),
    CONSTRAINT FK_Votes_Shareholder FOREIGN KEY (shareholder_id) REFERENCES SHAREHOLDERS(shareholder_id),
    CONSTRAINT FK_Votes_Snapshot    FOREIGN KEY (snapshot_id)    REFERENCES SHARE_SNAPSHOTS(snapshot_id),
    CONSTRAINT UQ_Votes UNIQUE (resolution_id, shareholder_id),   -- 1 co dong chi vote 1 lan / 1 nghi quyet
    CONSTRAINT CHK_Votes_Value CHECK (vote_value IN ('AGREE','DISAGREE','ABSTAIN'))
);
GO

/* ------------------------------------------------------------
   11. DOCUMENTS
   ------------------------------------------------------------ */
CREATE TABLE DOCUMENTS (
    document_id     INT IDENTITY(1,1) PRIMARY KEY,
    title           NVARCHAR(200) NOT NULL,
    description     NVARCHAR(MAX) NULL,
    file_url        VARCHAR(500)  NOT NULL,
    uploaded_at     DATETIME2     NOT NULL DEFAULT SYSDATETIME()
);
GO

/* ------------------------------------------------------------
   12. SHARE_ADJUSTMENT_LOGS
   ------------------------------------------------------------ */
CREATE TABLE SHARE_ADJUSTMENT_LOGS (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    shareholder_id  INT NOT NULL,
    old_value       INT NOT NULL,
    new_value       INT NOT NULL,
    reason          NVARCHAR(255) NULL,
    adjusted_by     INT NOT NULL,
    adjusted_at     DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_AdjLog_Shareholder FOREIGN KEY (shareholder_id) REFERENCES SHAREHOLDERS(shareholder_id),
    CONSTRAINT FK_AdjLog_User        FOREIGN KEY (adjusted_by)    REFERENCES USERS(user_id),
    CONSTRAINT CHK_AdjLog_Values CHECK (old_value >= 0 AND new_value >= 0)
);
GO

/* ------------------------------------------------------------
   13. AUDIT_LOGS
   ------------------------------------------------------------ */
CREATE TABLE AUDIT_LOGS (
    log_id          INT IDENTITY(1,1) PRIMARY KEY,
    user_id         INT NULL,
    action          VARCHAR(30) NOT NULL,
    entity_type     VARCHAR(30) NOT NULL,
    entity_id       INT NULL,        -- polymorphic reference theo entity_type, khong dat FK
    user_agent      VARCHAR(255) NULL,
    created_at      DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_Audit_User FOREIGN KEY (user_id) REFERENCES USERS(user_id),
    CONSTRAINT CHK_Audit_Action CHECK (action IN (
        'LOGIN','LOGOUT','CREATE','UPDATE','DELETE','TRANSFER','ADJUST_SHARE',
        'CREATE_ISSUE','SIGN_ISSUE','PROCESS_ISSUE',
        'CREATE_RESOLUTION','UPDATE_RESOLUTION','DELETE_RESOLUTION',
        'VOTE','UPLOAD_DOCUMENT','DELETE_DOCUMENT'
    )),
    CONSTRAINT CHK_Audit_EntityType CHECK (entity_type IN (
        'USER','SHARE','TRANSACTION','ISSUE','RESOLUTION','DOCUMENT','FINANCIAL_REPORT'
    ))
);
GO

/* ------------------------------------------------------------
   14. SCHEDULED_EVENTS
   ------------------------------------------------------------ */
CREATE TABLE SCHEDULED_EVENTS (
    event_id        INT IDENTITY(1,1) PRIMARY KEY,
    event_key       VARCHAR(100) NOT NULL,
    event_type      VARCHAR(30)  NOT NULL,
    issue_id        INT NULL,
    resolution_id   INT NULL,
    event_date      DATETIME2 NOT NULL,
    status          VARCHAR(20) NOT NULL,
    processed_at    DATETIME2 NULL,
    created_at      DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT UQ_ScheduledEvents_Key UNIQUE (event_key),
    CONSTRAINT FK_Sched_Issue      FOREIGN KEY (issue_id)      REFERENCES SHARE_ISSUES(issue_id),
    CONSTRAINT FK_Sched_Resolution FOREIGN KEY (resolution_id) REFERENCES RESOLUTIONS(resolution_id),
    -- OPEN_SHARE_ISSUE: chay luc snapshot_date, tao SHARE_SNAPSHOTS + sinh SHARE_ISSUE_DETAILS, mo ky
    -- PROCESS_SHARE_ISSUE: chay luc end_date, dong ky va cong co phan
    CONSTRAINT CHK_Sched_Type   CHECK (event_type IN ('OPEN_SHARE_ISSUE','PROCESS_SHARE_ISSUE','CLOSE_RESOLUTION')),
    CONSTRAINT CHK_Sched_Status CHECK (status IN ('PENDING','PROCESSING','COMPLETED','FAILED')),
    -- OPEN_SHARE_ISSUE / PROCESS_SHARE_ISSUE -> chi co issue_id | CLOSE_RESOLUTION -> chi co resolution_id
    CONSTRAINT CHK_Sched_TargetByType CHECK (
        (event_type = 'OPEN_SHARE_ISSUE'    AND issue_id IS NOT NULL AND resolution_id IS NULL)
        OR (event_type = 'PROCESS_SHARE_ISSUE' AND issue_id IS NOT NULL AND resolution_id IS NULL)
        OR (event_type = 'CLOSE_RESOLUTION'    AND resolution_id IS NOT NULL AND issue_id IS NULL)
    )
);
GO

/* ------------------------------------------------------------
   15. FINANCIAL_REPORTS
   ------------------------------------------------------------ */
CREATE TABLE FINANCIAL_REPORTS (
    report_id           INT IDENTITY(1,1) PRIMARY KEY,
    report_year         INT      NOT NULL,
    report_quarter      TINYINT  NOT NULL,
    revenue             DECIMAL(18,2) NULL,
    profit_before_tax   DECIMAL(18,2) NULL,
    profit_after_tax    DECIMAL(18,2) NULL,
    short_term_debt     DECIMAL(18,2) NULL,
    long_term_debt      DECIMAL(18,2) NULL,
    eps                 DECIMAL(10,2) NULL,
    pe                  DECIMAL(10,2) NULL,
    roe                 DECIMAL(5,2)  NULL,
    roa                 DECIMAL(5,2)  NULL,
    created_at          DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT UQ_FinReports_YearQuarter UNIQUE (report_year, report_quarter),
    CONSTRAINT CHK_FinReports_Year CHECK (report_year >= 2000),
    CONSTRAINT CHK_FinReports_Quarter CHECK (report_quarter BETWEEN 1 AND 4)
);
GO
ALTER TABLE DOCUMENTS ADD created_by INT NULL REFERENCES USERS(user_id) ON DELETE SET NULL;
ALTER TABLE FINANCIAL_REPORTS ADD created_by INT NULL REFERENCES USERS(user_id) ON DELETE SET NULL;
/* ============================================================
   HET FILE - Thu tu tao bang da tuan thu FK dependency:
   USERS -> SHAREHOLDERS -> SHARES -> SHARE_SNAPSHOTS ->
   SHARE_SNAPSHOT_DETAILS -> SHARE_ISSUES -> SHARE_ISSUE_DETAILS ->
   SHARE_TRANSACTIONS -> RESOLUTIONS -> VOTES -> DOCUMENTS ->
   SHARE_ADJUSTMENT_LOGS -> AUDIT_LOGS -> SCHEDULED_EVENTS ->
   FINANCIAL_REPORTS
   ============================================================ */
