package me.dmdev.rxpm.lint;

import com.android.annotations.NonNull;
import com.android.tools.lint.checks.CleanupDetector;
import com.android.tools.lint.detector.api.JavaContext;
import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiVariable;

import org.jetbrains.uast.UBinaryExpression;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UExpression;
import org.jetbrains.uast.ULocalVariable;
import org.jetbrains.uast.UReferenceExpression;
import org.jetbrains.uast.UReturnExpression;
import org.jetbrains.uast.UVariable;
import org.jetbrains.uast.UastCallKind;
import org.jetbrains.uast.UastUtils;
import org.jetbrains.uast.util.UastExpressionUtils;
import org.jetbrains.uast.visitor.AbstractUastVisitor;

import java.util.List;

/**
 * Visitor which checks whether an operation is "finished".
 * Copied from {@link CleanupDetector} with a little changes.
 *
 * In our case of PresentationModel we're looking for a "untilUnbind/untilDestroy" call.
 */
public abstract class FinishVisitor extends AbstractUastVisitor {
    protected final JavaContext mContext;
    protected final List<PsiVariable> mVariables;
    private final PsiVariable mOriginalVariableNode;

    private boolean mContainsCleanup;
    private boolean mEscapes;

    public FinishVisitor(JavaContext context, @NonNull PsiVariable variableNode) {
        mContext = context;
        mOriginalVariableNode = variableNode;
        mVariables = Lists.newArrayList(variableNode);
    }

    public boolean isCleanedUp() {
        return mContainsCleanup;
    }

    public boolean variableEscapes() {
        return mEscapes;
    }

    @Override
    public boolean visitElement(UElement node) {
        return mContainsCleanup || super.visitElement(node);
    }

    protected abstract boolean isCleanupCall(@NonNull UCallExpression call);

    @Override
    public boolean visitCallExpression(UCallExpression node) {
        if (node.getKind() == UastCallKind.METHOD_CALL) {
            visitMethodCallExpression(node);
        }
        return super.visitCallExpression(node);
    }

    private void visitMethodCallExpression(UCallExpression call) {
        if (mContainsCleanup) {
            return;
        }

        // Look for escapes
        if (!mEscapes) {
            for (UExpression expression : call.getValueArguments()) {
                if (expression instanceof UReferenceExpression) {
                    PsiElement resolved = ((UReferenceExpression) expression).resolve();
                    //noinspection SuspiciousMethodCalls
                    if (resolved != null && mVariables.contains(resolved)) {
                        mEscapes = true;
                    }
                }
            }
        }

        if (isCleanupCall(call)) {
            mContainsCleanup = true;
        }
    }

    @Override
    public boolean visitVariable(UVariable variable) {
        if (variable instanceof ULocalVariable) {
            UExpression initializer = variable.getUastInitializer();
            if (initializer instanceof UReferenceExpression) {
                PsiElement resolved = ((UReferenceExpression) initializer).resolve();
                //noinspection SuspiciousMethodCalls
                if (resolved != null && mVariables.contains(resolved)) {
                    mVariables.add(variable.getPsi());
                }
            }
        }

        return super.visitVariable(variable);
    }

    @Override
    public boolean visitBinaryExpression(UBinaryExpression expression) {
        if (!UastExpressionUtils.isAssignment(expression)) {
            return super.visitBinaryExpression(expression);
        }

        // TEMPORARILY DISABLED; see testDatabaseCursorReassignment
        // This can result in some false positives right now. Play it
        // safe instead.
        boolean clearLhs = false;

        UExpression rhs = expression.getRightOperand();
        if (rhs instanceof UReferenceExpression) {
            PsiElement resolved = ((UReferenceExpression) rhs).resolve();
            //noinspection SuspiciousMethodCalls
            if (resolved != null && mVariables.contains(resolved)) {
                clearLhs = false;
                PsiElement lhs = UastUtils.tryResolve(expression.getLeftOperand());
                if (lhs instanceof PsiLocalVariable) {
                    mVariables.add(((PsiLocalVariable) lhs));
                } else if (lhs instanceof PsiField) {
                    mEscapes = true;
                }
            }
        }

        //noinspection ConstantConditions
        if (clearLhs) {
            // If we reassign one of the variables, clear it out
            PsiElement lhs = UastUtils.tryResolve(expression.getLeftOperand());
            //noinspection SuspiciousMethodCalls
            if (lhs != null && !lhs.equals(mOriginalVariableNode)
                    && mVariables.contains(lhs)) {
                //noinspection SuspiciousMethodCalls
                mVariables.remove(lhs);
            }
        }

        return super.visitBinaryExpression(expression);
    }

    @Override
    public boolean visitReturnExpression(UReturnExpression node) {
        UExpression returnValue = node.getReturnExpression();
        if (returnValue instanceof UReferenceExpression) {
            PsiElement resolved = ((UReferenceExpression) returnValue).resolve();
            //noinspection SuspiciousMethodCalls
            if (resolved != null && mVariables.contains(resolved)) {
                mEscapes = true;
            }
        }

        return super.visitReturnExpression(node);
    }
}

