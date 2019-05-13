cd /home/travis/build/passed/*/*
rm -rf .git
if [ -d "reproduce_tmp" ]; then rm -Rf reproduce_tmp; fi
find * -maxdepth 0 -regextype posix-extended -regex ".+-.{40}.*" -prune -exec sudo rm -rf {} \;

cd /home/travis/build/failed/*/*

REPO="`pwd | sed -e "s/\/home\/travis\/build\/failed\///"`"

rm -rf .git
git init
git config --global user.email "BugSwarm@BugSwarm.com"
git config --global user.name "BugSwarm"

find * -maxdepth 0 -regextype posix-extended -regex ".+-.{40}.*" -prune -exec sudo rm -rf {} \;

if [ -d "reproduce_tmp" ]; then rm -Rf reproduce_tmp; fi

echo "Buggy Version"
rm -rf target test-output test-output-tests
git add --all -f .
git commit -m "Buggy version of $2" > /dev/null
mv .git ../
sudo rm -rf .* 2> /dev/null
mv ../.git .

echo "Passed Version"
cp -rT "/home/travis/build/passed/$REPO" .
rm -rf target test-output test-output-tests

git diff > /bugswarm-sandbox/patch.diff
git add --all -f .
git commit --allow-empty -m "Passed version of $2"
git push https://$USER:$PASSWORD@github.com/$REPO master:$2 -f