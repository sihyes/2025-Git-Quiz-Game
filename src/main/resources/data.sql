-- 1. Git 初始化与配置
INSERT INTO question (question_text, answer, score, category, optiona, optionb, optionc, optiond) 
VALUES ('Git 저장소를 초기화하는 명령어는?', 'git init', 10, 'basic', 'git start', 'git init', 'git create', 'git new');

INSERT INTO question (question_text, answer, score, category, optiona, optionb, optionc, optiond) 
VALUES ('전역 사용자 이름을 설정하는 올바른 명령어는?', 'git config --global user.name "Name"', 10, 'basic', 'git setup user "Name"', 'git config --global user.name "Name"', 'git user --name "Name"', 'git global user "Name"');

-- 2. Staging & Committing
INSERT INTO question (question_text, answer, score, category, optiona, optionb, optionc, optiond) 
VALUES ('파일의 변경사항을 Staging Area에 추가하는 명령어는?', 'git add', 10, 'basic', 'git stage', 'git commit', 'git add', 'git push');

INSERT INTO question (question_text, answer, score, category, optiona, optionb, optionc, optiond) 
VALUES ('커밋 메시지와 함께 변경사항을 저장소에 기록하는 명령어는?', 'git commit -m "msg"', 15, 'basic', 'git save "msg"', 'git commit -m "msg"', 'git log "msg"', 'git record "msg"');

-- 3. Branching & Merging
INSERT INTO question (question_text, answer, score, category, optiona, optionb, optionc, optiond) 
VALUES ('새로운 브랜치 "feature"를 생성하고 해당 브랜치로 이동하는 명령어는?', 'git checkout -b feature', 20, 'command', 'git branch feature', 'git checkout feature', 'git checkout -b feature', 'git move feature');

INSERT INTO question (question_text, answer, score, category, optiona, optionb, optionc, optiond) 
VALUES ('현재 브랜치에 "dev" 브랜치를 병합(Merge)하는 명령어는?', 'git merge dev', 20, 'command', 'git combine dev', 'git merge dev', 'git join dev', 'git pull dev');

-- 4. Remote Repository
INSERT INTO question (question_text, answer, score, category, optiona, optionb, optionc, optiond) 
VALUES ('원격 저장소의 변경사항을 가져와서 현재 브랜치와 병합하는 명령어는?', 'git pull', 15, 'concept', 'git fetch', 'git push', 'git clone', 'git pull');

INSERT INTO question (question_text, answer, score, category, optiona, optionb, optionc, optiond) 
VALUES ('원격 저장소의 이름이 "origin"일 때, master 브랜치로 코드를 업로드하는 명령어는?', 'git push origin master', 15, 'command', 'git upload origin master', 'git push origin master', 'git send origin master', 'git commit origin master');

-- 5. Advanced
INSERT INTO question (question_text, answer, score, category, optiona, optionb, optionc, optiond) 
VALUES ('직전 커밋 메시지를 수정하고 싶을 때 사용하는 옵션은?', '--amend', 30, 'advanced', '--fix', '--change', '--amend', '--modify');

INSERT INTO question (question_text, answer, score, category, optiona, optionb, optionc, optiond) 
VALUES ('Git의 작업 트리 상태를 확인하는 명령어는?', 'git status', 10, 'basic', 'git info', 'git check', 'git status', 'git state');
