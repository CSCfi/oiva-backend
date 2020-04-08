alter table muutospyynto
    add column alkupera varchar(10) not null default 'KJ',
    add column asianumero varchar(16) default null,
    add column paatospvm date default null;

-- drop default value to force explicit definition
alter table muutospyynto
    alter column alkupera drop default;