
    alter table jbid_io 
        drop 
        foreign key FK94860092CF26285;

    alter table jbid_io 
        drop 
        foreign key FK948600921900168B;

    alter table jbid_io_attr 
        drop 
        foreign key FK4DC61D7EDE7E39CB;

    alter table jbid_io_attr 
        drop 
        foreign key FK4DC61D7E992317F0;

    alter table jbid_io_attr_text_values 
        drop 
        foreign key FKC6F8C733577FACCB;

    alter table jbid_io_creden 
        drop 
        foreign key FKF7FB3EC4F1FBD584;

    alter table jbid_io_creden 
        drop 
        foreign key FKF7FB3EC4992317F0;

    alter table jbid_io_creden 
        drop 
        foreign key FKF7FB3EC429C64409;

    alter table jbid_io_creden_props 
        drop 
        foreign key FKBAD648353BD68084;

    alter table jbid_io_props 
        drop 
        foreign key FK6BCFF78372655F8D;

    alter table jbid_io_rel 
        drop 
        foreign key FKE9BC4F6C2D9E6CE8;

    alter table jbid_io_rel 
        drop 
        foreign key FKE9BC4F6C4EF6CBA4;

    alter table jbid_io_rel 
        drop 
        foreign key FKE9BC4F6CE5991E18;

    alter table jbid_io_rel 
        drop 
        foreign key FKE9BC4F6C89DEF9C9;

    alter table jbid_io_rel_name 
        drop 
        foreign key FK5856A77ECF26285;

    alter table jbid_io_rel_name_props 
        drop 
        foreign key FK224ABD6F3C564090;

    alter table jbid_io_rel_props 
        drop 
        foreign key FKB2A23ADD93AFB8E5;

    alter table jbid_real_props 
        drop 
        foreign key FK5F651EFBF40F776D;

    drop table if exists jbid_attr_bin_value;

    drop table if exists jbid_creden_bin_value;

    drop table if exists jbid_io;

    drop table if exists jbid_io_attr;

    drop table if exists jbid_io_attr_text_values;

    drop table if exists jbid_io_creden;

    drop table if exists jbid_io_creden_props;

    drop table if exists jbid_io_creden_type;

    drop table if exists jbid_io_props;

    drop table if exists jbid_io_rel;

    drop table if exists jbid_io_rel_name;

    drop table if exists jbid_io_rel_name_props;

    drop table if exists jbid_io_rel_props;

    drop table if exists jbid_io_rel_type;

    drop table if exists jbid_io_type;

    drop table if exists jbid_real_props;

    drop table if exists jbid_realm;
