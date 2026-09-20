# Contributing to Loom

Thanks for your interest in improving Loom. Bug reports, fixes, and focused
feature contributions are all welcome.

## Before you start

- For anything beyond a trivial fix, open an issue first so we can agree on the
  approach before you invest time.
- Check existing issues and pull requests to avoid duplicate work.

## Development

Loom is a Clojure and ClojureScript library. You need a JDK and either
[Leiningen](https://leiningen.org) or the
[Clojure CLI](https://clojure.org/guides/install_clojure).

```bash
lein test          # default suite
lein test :all     # includes the slow generative specs
clojure -M:test-cljs # the suite under ClojureScript on Node (needs Node.js)
lein all test      # Clojure 1.10, 1.11 and 1.12
clojure -M:test    # the same suite with the Clojure CLI
```

We can merge a change if it meets these conditions:

- **Tests first.** Add or update tests for the behavior you change. For a bug
  fix, include a regression test that fails before your fix and passes after.
- **Green build.** The suite passes on Clojure 1.10, 1.11 and 1.12.
- **One logical change** per pull request.

Code in `.cljc` files runs on both Clojure and ClojureScript. Keep it portable.

## Commits and pull requests

- Follow [the seven rules of a great Git commit message](https://chris.beams.io/posts/git-commit/#seven-rules):
  an imperative subject under about 72 characters, then a body that explains why.
  [Conventional Commits](https://www.conventionalcommits.org/) are welcome
  (`fix:`, `feat:`, `docs:`, `test:`).
- Update `CHANGELOG.md` when your change is user-visible.
- Rebase on the latest `master` before you open the pull request.

## License

Loom is distributed under the Eclipse Public License 1.0. If you contribute, you
agree that this license applies to your contributions.
