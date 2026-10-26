CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_created_at ON tasks(created_at);
