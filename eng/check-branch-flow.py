#!/usr/bin/env python3
"""Prove the squash promotion procedure in a temporary Git repository."""

from pathlib import Path
import subprocess
import tempfile


def git(repo, *args):
    return subprocess.run(
        ["git", *args], cwd=repo, check=True, capture_output=True, text=True
    ).stdout.strip()


def require(condition, message):
    if not condition:
        raise AssertionError(message)


def ancestor(repo, older, newer):
    result = subprocess.run(
        ["git", "merge-base", "--is-ancestor", older, newer], cwd=repo, check=False
    )
    require(result.returncode in (0, 1), "Git ancestry check failed.")
    return result.returncode == 0


def tree(repo, revision):
    return git(repo, "rev-parse", revision + "^{tree}")


def commit(repo, message):
    git(repo, "add", "--all")
    git(repo, "commit", "-m", message)
    return git(repo, "rev-parse", "HEAD")


def baseline_matches(repo, previous_source, source):
    return ancestor(repo, previous_source, source) and tree(
        repo, previous_source
    ) == tree(repo, "main")


def prepare(repo, previous_source, name, expected_paths):
    source = git(repo, "rev-parse", "dev")
    require(baseline_matches(repo, previous_source, source), "Main baseline differs.")
    git(repo, "switch", "-c", name, source)
    git(repo, "merge", "--no-ff", "--strategy=ours", "main", "-m", "chore: prepare promotion")
    require(tree(repo, "HEAD") == tree(repo, source), "Promotion changed the dev tree.")
    require(ancestor(repo, "main", "HEAD"), "Promotion does not include current main.")
    paths = git(repo, "diff", "--name-only", "main...HEAD").splitlines()
    require(paths == expected_paths, "Promotion contains unexpected paths: " + repr(paths))
    require(
        git(repo, "diff", "main...HEAD") == git(repo, "diff", "main", source),
        "PR diff differs from the intended tree diff.",
    )
    print(name, source, git(repo, "rev-parse", "main"), tree(repo, "HEAD"), sep="\t")
    return source


def promote(repo, previous_source, name, expected_paths):
    source = prepare(repo, previous_source, name, expected_paths)
    git(repo, "switch", "main")
    git(repo, "merge", "--squash", name)
    commit(repo, "chore: squash promotion")
    require(tree(repo, "main") == tree(repo, source), "Released tree differs from dev.")
    require(git(repo, "rev-parse", "dev") == source, "Promotion moved dev.")
    require(
        len(git(repo, "rev-list", "--parents", "-n", "1", "main").split()) == 2,
        "Squash commit must have one parent.",
    )
    require(
        not git(repo, "rev-list", "--min-parents=2", "main", "dev"),
        "Protected branch histories contain a merge commit.",
    )
    print("PASS: released tree equals source; main/dev histories remain linear.")
    return source


def main():
    with tempfile.TemporaryDirectory(prefix="cistula-branch-flow-") as directory:
        repo = Path(directory)
        git(repo, "init", "--initial-branch=main")
        git(repo, "config", "user.name", "Branch flow verification")
        git(repo, "config", "user.email", "verification@example.invalid")
        git(repo, "config", "commit.gpgsign", "false")
        (repo / "base.txt").write_text("base\n")
        commit(repo, "chore: bootstrap")
        git(repo, "switch", "-c", "dev")
        (repo / "old.txt").write_text("already promoted\n")
        previous_source = commit(repo, "feat: old change")
        git(repo, "switch", "main")
        git(repo, "merge", "--squash", "dev")
        commit(repo, "chore: legacy promotion")
        require(tree(repo, "main") == tree(repo, "dev"), "Initial trees differ.")
        require(not ancestor(repo, "main", "dev"), "Legacy histories must diverge.")
        require(not ancestor(repo, "dev", "main"), "Legacy histories must diverge.")

        git(repo, "switch", "dev")
        (repo / "next.txt").write_text("next change\n")
        commit(repo, "feat: next change")
        require(
            git(repo, "diff", "--name-only", "main...dev").splitlines()
            == ["next.txt", "old.txt"],
            "Legacy PR diff no longer reproduces the repeated change.",
        )
        print("PASS: legacy direct PR repeats old.txt and cannot fast-forward dev to main.")
        print("branch\tsource\tmain_before\tpromotion_tree")
        previous_source = promote(repo, previous_source, "chore/promotion-1", ["next.txt"])

        git(repo, "switch", "dev")
        (repo / "next.txt").write_text("second change\n")
        (repo / "release2.txt").write_text("second promotion\n")
        git(repo, "rm", "old.txt")
        commit(repo, "feat: second change")
        previous_source = promote(
            repo, previous_source, "chore/promotion-2", ["next.txt", "old.txt", "release2.txt"]
        )

        git(repo, "switch", "dev")
        (repo / "later.txt").write_text("later change\n")
        source = commit(repo, "feat: later change")
        prepare(repo, previous_source, "chore/promotion-later", ["later.txt"])
        require(git(repo, "rev-parse", "dev") == source, "Preparation moved dev.")

        git(repo, "switch", "main")
        (repo / "main-only.txt").write_text("unexpected main change\n")
        commit(repo, "chore: unexpected main change")
        require(
            not baseline_matches(repo, previous_source, source),
            "Baseline check accepted an unexpected main-only change.",
        )
        require(
            not ancestor(repo, "main", "chore/promotion-later"),
            "An advanced main must invalidate the prepared branch.",
        )
        print("PASS: main-only content and an advanced main invalidate preparation.")
    print("PASS: two promotions and a later dev change satisfy the procedure.")


if __name__ == "__main__":
    main()
