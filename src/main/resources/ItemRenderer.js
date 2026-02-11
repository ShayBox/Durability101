var Opcodes = Java.type("org.objectweb.asm.Opcodes");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");

function initializeCoreMod() {
    return {
        'durability101': {
            'target': {
                'type': 'CLASS',
                'name': '${coremod_target_class}'
            },
            'transformer': function (classNode) {
                var methods = classNode.methods;
                for (m in methods) {
                    var method = methods[m];
                    if (method.name === "renderGuiItemDecorations" || method.name === "${coremod_obf_method}") {
                        var instructions = method.instructions;
                        var firstInstruction = instructions.get(0);

                        // Parameters
                        instructions.insertBefore(firstInstruction, new VarInsnNode(Opcodes.ALOAD, 1));
                        instructions.insertBefore(firstInstruction, new VarInsnNode(Opcodes.ALOAD, 2));
                        instructions.insertBefore(firstInstruction, new VarInsnNode(Opcodes.ILOAD, 3));
                        instructions.insertBefore(firstInstruction, new VarInsnNode(Opcodes.ILOAD, 4));

                        // Method
                        var renderDurability101 = new MethodInsnNode(Opcodes.INVOKESTATIC, "com/shaybox/durability101/Durability101", "renderDurability101", "(L${coremod_font_class};L${coremod_itemstack_class};II)V", false);
                        instructions.insertBefore(firstInstruction, renderDurability101);
                    }
                }
                return classNode;
            }
        }
    }
}
