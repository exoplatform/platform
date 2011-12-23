
    alter table jbid_io 
        drop constraint FK94860092CF26285;

    alter table jbid_io 
        drop constraint FK948600921900168B;

    alter table jbid_io_attr 
        drop constraint FK4DC61D7EDE7E39CB;

    alter table jbid_io_attr 
        drop constraint FK4DC61D7E992317F0;

    alter table jbid_io_attr_text_values 
        drop constraint FKC6F8C733577FACCB;

    alter table jbid_io_creden 
        drop constraint FKF7FB3EC4F1FBD584;

    alter table jbid_io_creden 
        drop constraint FKF7FB3EC4992317F0;

    alter table jbid_io_creden 
        drop constraint FKF7FB3EC429C64409;

    alter table jbid_io_creden_props 
        drop constraint FKBAD648353BD68084;

    alter table jbid_io_props 
        drop constraint FK6BCFF78372655F8D;

    alter table jbid_io_rel 
        drop constraint FKE9BC4F6C2D9E6CE8;

    alter table jbid_io_rel 
        drop constraint FKE9BC4F6C4EF6CBA4;

    alter table jbid_io_rel 
        drop constraint FKE9BC4F6CE5991E18;

    alter table jbid_io_rel 
        drop constraint FKE9BC4F6C89DEF9C9;

    alter table jbid_io_rel_name 
        drop constraint FK5856A77ECF26285;

    alter table jbid_io_rel_name_props 
        drop constraint FK224ABD6F3C564090;

    alter table jbid_io_rel_props 
        drop constraint FKB2A23ADD93AFB8E5;

    alter table jbid_real_props 
        drop constraint FK5F651EFBF40F776D;

    drop table jbid_attr_bin_value;

    drop table jbid_creden_bin_value;

    drop table jbid_io;

    drop table jbid_io_attr;

    drop table jbid_io_attr_text_values;

    drop table jbid_io_creden;

    drop table jbid_io_creden_props;

    drop table jbid_io_creden_type;

    drop table jbid_io_props;

    drop table jbid_io_rel;

    drop table jbid_io_rel_name;

    drop table jbid_io_rel_name_props;

    drop table jbid_io_rel_props;

    drop table jbid_io_rel_type;

    drop table jbid_io_type;

    drop table jbid_real_props;

    drop table jbid_realm;

    drop sequence hibernate_sequence;
