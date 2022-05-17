select * from user_sys_privs;
--Para dar permisos al usuario c##DEB666 y que pueda hacer debugging

CONNECT SYS AS SYSDBA;
GRANT DEBUG ANY PROCEDURE TO C##DEB666;
GRANT DEBUG CONNECT SESSION TO C##DEB666;

BEGIN
    dbms_netwoRK_ACL_ADMIN.APPEND_HOST_ACE
    (
    host => '127.0.0.1',
    lower_port => null,
    upper_port => null,
    ace => xs$ace_type(privilege_list => xs$name_list('jdwp'),
    principal_name => 'C##DEB666',
    principal_type => xs_acl.ptype_db)
    );
end;