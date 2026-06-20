with open("src/main/java/com/example/security/authorization/rbac/CustomPermissionEvaluator.java", "r") as f:
    lines = f.readlines()

new_lines = []
in_conflict = False
for line in lines:
    if line.startswith("<<<<<<< HEAD"):
        in_conflict = True
        new_lines.append("        // Fast path: Check for ROLE_ADMIN first\n")
        new_lines.append("        boolean isAdmin = auth.getAuthorities().stream()\n")
        new_lines.append("                .anyMatch(grantedAuth -> \"ROLE_ADMIN\".equals(grantedAuth.getAuthority()));\n")
        new_lines.append("        if (isAdmin) {\n")
        new_lines.append("            return true;\n")
        new_lines.append("        }\n\n")
        new_lines.append("        String requiredAuthority = String.format(\"%s_%s\", targetType, permission);\n")
        new_lines.append("        return auth.getAuthorities().stream()\n")
        new_lines.append("                .anyMatch(grantedAuth -> requiredAuthority.equals(grantedAuth.getAuthority()));\n")
    elif line.startswith("======="):
        pass
    elif line.startswith(">>>>>>> main"):
        in_conflict = False
    elif not in_conflict:
        new_lines.append(line)

with open("src/main/java/com/example/security/authorization/rbac/CustomPermissionEvaluator.java", "w") as f:
    f.writelines(new_lines)
