INSERT INTO DGB_LISTENER_LOG (
    LISTENERLOG_INSTANCE,
    LISTENERLOG_LISTENER_NAME,
    LISTENERLOG_LISTENER_UUID,
    LISTENERLOG_STATUS,
    LISTENERLOG_MESSAGE,
    LISTENERLOG_CONTEXT,
    LISTENERLOG_TIMESTAMP)
VALUES (?,?,?,?,?,?,$CURRENT_TIMESTAMP$)