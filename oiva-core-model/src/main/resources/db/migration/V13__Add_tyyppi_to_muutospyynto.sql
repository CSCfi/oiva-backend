alter table muutospyynto
    add column alkupera varchar(10) not null default 'KJ';

-- drop default value to force explicit definition
alter table muutospyynto
    alter column alkupera drop default;