var Opcodes = Java.type("org.objectweb.asm.Opcodes");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");

function initializeCoreMod() {
    return {
        'durability101': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.renderer.entity.ItemRenderer'
            },
            'transformer': function (classNode) {
                var methods = classNode.methods;
                for (m in methods) {
                    var method = methods[m];
                    if (method.name === "renderGuiItemDecorations" || method.name === "m_115174_") {
                        var instructions = method.instructions;
                        var firstInstruction = instructions.get(0);

                        // Parameters
                        instructions.insertBefore(firstInstruction, new VarInsnNode(Opcodes.ALOAD, 1)); // Font
                        instructions.insertBefore(firstInstruction, new VarInsnNode(Opcodes.ALOAD, 2)); // ItemStack
                        instructions.insertBefore(firstInstruction, new VarInsnNode(Opcodes.ILOAD, 3)); // xPosition
                        instructions.insertBefore(firstInstruction, new VarInsnNode(Opcodes.ILOAD, 4)); // yPosition

                        // Method
                        var renderDurability101 = new MethodInsnNode(Opcodes.INVOKESTATIC, "com/shaybox/durability101/Main", "renderDurability101", "(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", false);
                        instructions.insertBefore(firstInstruction, renderDurability101); // renderDurability101
                    }
                }
                return classNode;
            }
        }
    }
}
